package com.eralp.ecommerce;

import com.eralp.ecommerce.client.PaymentGateway;
import com.eralp.ecommerce.entity.Cart;
import com.eralp.ecommerce.entity.CartItem;
import com.eralp.ecommerce.entity.Category;
import com.eralp.ecommerce.entity.IdempotencyStatus;
import com.eralp.ecommerce.entity.Product;
import com.eralp.ecommerce.entity.Role;
import com.eralp.ecommerce.entity.User;
import com.eralp.ecommerce.exception.TransientPaymentException;
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

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class OrderCheckoutPaymentRetryIntegrationTest {

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
    }

    @Test
    void shouldRetryPaymentOnTransientFailureAndEventuallySucceed() throws Exception {
        prepareCart("test@example.com");

        AtomicInteger counter = new AtomicInteger(0);
        doAnswer(invocation -> {
            if (counter.getAndIncrement() < 2) {
                throw new TransientPaymentException("temporary failure");
            }
            return null;
        })
                .when(paymentGateway)
                .authorize(anyLong(), any(BigDecimal.class), anyString());

        OrderResponse response = checkout("test@example.com", "retry-key");
        Long orderId = response.getOrderId();

        verify(paymentGateway, times(3)).authorize(anyLong(), any(BigDecimal.class), anyString());
        assertThat(counter.get()).isEqualTo(3);
        assertThat(orderRepository.count()).isEqualTo(1L);
        assertThat(orderId).isNotNull();
        assertThat(idempotencyRecordRepository.findByIdempotencyKey("retry-key").orElseThrow().getStatus())
                .isEqualTo(IdempotencyStatus.SUCCESS);
        assertThat(idempotencyRecordRepository.findByIdempotencyKey("retry-key").orElseThrow().getResponseOrderId())
                .isEqualTo(orderId);
    }

    @Test
    void shouldNotRetryWhenCheckoutFailsDueToPermanentBusinessError() throws Exception {
        doNothing().when(paymentGateway).authorize(anyLong(), any(BigDecimal.class), anyString());

        performCheckout("test@example.com", "empty-cart-key")
                .andExpect(status().isBadRequest());

        verify(paymentGateway, never()).authorize(anyLong(), any(BigDecimal.class), anyString());
        assertThat(orderRepository.count()).isEqualTo(0L);
        assertThat(idempotencyRecordRepository.findByIdempotencyKey("empty-cart-key").orElseThrow().getStatus())
                .isEqualTo(IdempotencyStatus.FAILED);
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

}
