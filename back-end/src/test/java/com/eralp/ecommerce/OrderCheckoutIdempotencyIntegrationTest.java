package com.eralp.ecommerce;

import com.eralp.ecommerce.client.PaymentGateway;
import com.eralp.ecommerce.entity.Cart;
import com.eralp.ecommerce.entity.CartItem;
import com.eralp.ecommerce.entity.Category;
import com.eralp.ecommerce.entity.IdempotencyOperationType;
import com.eralp.ecommerce.entity.IdempotencyRecord;
import com.eralp.ecommerce.entity.IdempotencyStatus;
import com.eralp.ecommerce.entity.Product;
import com.eralp.ecommerce.entity.Role;
import com.eralp.ecommerce.entity.User;
import com.eralp.ecommerce.messaging.OrderEventProducer;
import com.eralp.ecommerce.dto.order.OrderResponse;
import com.eralp.ecommerce.repository.CartItemRepository;
import com.eralp.ecommerce.repository.CartRepository;
import com.eralp.ecommerce.repository.CategoryRepository;
import com.eralp.ecommerce.repository.IdempotencyRecordRepository;
import com.eralp.ecommerce.repository.OrderRepository;
import com.eralp.ecommerce.repository.ProductRepository;
import com.eralp.ecommerce.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class OrderCheckoutIdempotencyIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private IdempotencyRecordRepository idempotencyRecordRepository;

    @MockBean
    private OrderEventProducer orderEventProducer;

    @MockBean
    private PaymentGateway paymentGateway;

    private User testUser;
    private Category category;

    @BeforeEach
    void setUp() {
        testUser = createUserWithCart("test@example.com");
        category = createCategory("electronics");
        prepareCart("test@example.com");
    }

    @Test
    void shouldReturnSameOrderForRepeatedCheckoutWithSameIdempotencyKey() throws Exception {
        OrderResponse firstResponse = checkout("test@example.com", "same-key-1");
        OrderResponse secondResponse = checkout("test@example.com", "same-key-1");
        Long firstOrderId = firstResponse.getOrderId();
        Long secondOrderId = secondResponse.getOrderId();

        assertThat(firstOrderId).isNotNull();
        assertThat(secondOrderId).isNotNull();
        assertThat(firstOrderId).isEqualTo(secondOrderId);
        assertThat(orderRepository.count()).isEqualTo(1L);
        assertThat(idempotencyRecordRepository.count()).isEqualTo(1L);

        IdempotencyRecord record = idempotencyRecordRepository.findByIdempotencyKey("same-key-1").orElseThrow();
        assertThat(record.getStatus()).isEqualTo(IdempotencyStatus.SUCCESS);
        assertThat(record.getResponseOrderId()).isEqualTo(firstOrderId);
    }

    @Test
    void shouldCreateNewOrderForDifferentIdempotencyKeys() throws Exception {
        OrderResponse firstResponse = checkout("test@example.com", "key-1");
        Long firstOrderId = firstResponse.getOrderId();

        prepareCart("test@example.com");

        OrderResponse secondResponse = checkout("test@example.com", "key-2");
        Long secondOrderId = secondResponse.getOrderId();

        assertThat(firstOrderId).isNotEqualTo(secondOrderId);
        assertThat(orderRepository.count()).isEqualTo(2L);
        assertThat(idempotencyRecordRepository.count()).isEqualTo(2L);

        IdempotencyRecord firstRecord = idempotencyRecordRepository.findByIdempotencyKey("key-1").orElseThrow();
        IdempotencyRecord secondRecord = idempotencyRecordRepository.findByIdempotencyKey("key-2").orElseThrow();
        assertThat(firstOrderId).isNotNull();
        assertThat(secondOrderId).isNotNull();
        assertThat(firstRecord.getStatus()).isEqualTo(IdempotencyStatus.SUCCESS);
        assertThat(secondRecord.getStatus()).isEqualTo(IdempotencyStatus.SUCCESS);
        assertThat(firstRecord.getResponseOrderId()).isEqualTo(firstOrderId);
        assertThat(secondRecord.getResponseOrderId()).isEqualTo(secondOrderId);
    }

    @Test
    void shouldCreateSingleOrderForConcurrentRequestsWithSameKey() throws Exception {
        String email = "test@example.com";
        String key = "concurrent-key";

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);

        Callable<Integer> task = () -> {
            ready.countDown();
            if (!start.await(5, TimeUnit.SECONDS)) {
                throw new IllegalStateException("Failed to coordinate concurrent test start");
            }
            MvcResult result = performCheckout(email, key).andReturn();
            return result.getResponse().getStatus();
        };

        Future<Integer> first = executor.submit(task);
        Future<Integer> second = executor.submit(task);

        ready.await(5, TimeUnit.SECONDS);
        start.countDown();

        Integer firstStatus = first.get(5, TimeUnit.SECONDS);
        Integer secondStatus = second.get(5, TimeUnit.SECONDS);
        executor.shutdownNow();

        assertThat(orderRepository.count()).isEqualTo(1L);
        assertThat(idempotencyRecordRepository.count()).isEqualTo(1L);
        assertThat(firstStatus).isIn(201, 409);
        assertThat(secondStatus).isIn(201, 409);
        assertThat(Set.of(firstStatus, secondStatus)).contains(201);
    }

    @Test
    void shouldReturnConflictWhenCheckoutIsAlreadyProcessingForSameKey() throws Exception {
        IdempotencyRecord record = new IdempotencyRecord();
        record.setIdempotencyKey("processing-key");
        record.setUserId(testUser.getId());
        record.setOperationType(IdempotencyOperationType.CHECKOUT);
        record.setRequestHash(expectedCheckoutHash(testUser.getId()));
        record.setStatus(IdempotencyStatus.PROCESSING);
        idempotencyRecordRepository.save(record);

        performCheckout("test@example.com", "processing-key")
                .andExpect(status().isConflict());

        assertThat(orderRepository.count()).isEqualTo(0L);
        assertThat(idempotencyRecordRepository.count()).isEqualTo(1L);
        assertThat(idempotencyRecordRepository.findByIdempotencyKey("processing-key").orElseThrow().getStatus())
                .isEqualTo(IdempotencyStatus.PROCESSING);
    }

    private OrderResponse checkout(String email, String idempotencyKey) throws Exception {
        String responseBody = performCheckout(email, idempotencyKey)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readValue(responseBody, OrderResponse.class);
    }

    private org.springframework.test.web.servlet.ResultActions performCheckout(String email, String idempotencyKey) throws Exception {
        return mockMvc.perform(
                post("/api/v1/orders/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.user(email).roles("USER"))
                        .header("Idempotency-Key", idempotencyKey)
        );
    }

    private User createUserWithCart(String email) {
        User user = new User();
        user.setEmail(email);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setRole(Role.ROLE_USER);
        user = userRepository.save(user);

        Cart cart = new Cart();
        cart.setUser(user);
        cartRepository.save(cart);
        return user;
    }

    private Category createCategory(String name) {
        Category newCategory = new Category();
        newCategory.setName(name);
        newCategory.setDescription("test-category");
        return categoryRepository.save(newCategory);
    }

    private void prepareCart(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        Cart cart = cartRepository.findByUserId(user.getId()).orElseThrow();
        cartItemRepository.deleteAllByCartId(cart.getId());

        addCartItem(user, "Prepared Product", new BigDecimal("100.00"), 1);
    }

    private void addCartItem(User user, String productName, BigDecimal price, int quantity) {
        Cart cart = cartRepository.findByUserId(user.getId()).orElseThrow();
        Product product = new Product();
        product.setName(productName);
        product.setDescription("test-product");
        product.setPrice(price);
        product.setStock(100);
        product.setCategory(category);
        product = productRepository.save(product);

        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);
        cartItem.setUnitPrice(price);
        cartItemRepository.save(cartItem);
    }

    private String expectedCheckoutHash(Long userId) {
        Cart cart = cartRepository.findByUserId(userId).orElseThrow();
        List<CartItem> cartItems = cartItemRepository.findAllByCartId(cart.getId());
        String cartSnapshot = cartItems.stream()
                .sorted(java.util.Comparator.comparing(cartItem -> cartItem.getProduct().getId()))
                .map(cartItem -> cartItem.getProduct().getId() + "-" + cartItem.getQuantity())
                .collect(java.util.stream.Collectors.joining(","));
        String hashInput = userId + ":" + cartSnapshot;

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(hashInput.getBytes(StandardCharsets.UTF_8));
            return String.format("%064x", new BigInteger(1, hashedBytes));
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }

}
