package com.eralp.ecommerce.integration;

import com.eralp.ecommerce.client.PaymentGateway;
import com.eralp.ecommerce.entity.Cart;
import com.eralp.ecommerce.entity.CartItem;
import com.eralp.ecommerce.entity.Category;
import com.eralp.ecommerce.entity.IdempotencyRecord;
import com.eralp.ecommerce.entity.IdempotencyStatus;
import com.eralp.ecommerce.entity.Product;
import com.eralp.ecommerce.entity.Role;
import com.eralp.ecommerce.entity.User;
import com.eralp.ecommerce.messaging.OrderEventProducer;
import com.eralp.ecommerce.repository.CartItemRepository;
import com.eralp.ecommerce.repository.CartRepository;
import com.eralp.ecommerce.repository.CategoryRepository;
import com.eralp.ecommerce.repository.IdempotencyRecordRepository;
import com.eralp.ecommerce.repository.OrderItemRepository;
import com.eralp.ecommerce.repository.OrderRepository;
import com.eralp.ecommerce.repository.ProductRepository;
import com.eralp.ecommerce.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrderIntegrationTest {

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
    private OrderItemRepository orderItemRepository;

    @Autowired
    private IdempotencyRecordRepository idempotencyRecordRepository;

    @MockBean
    private OrderEventProducer orderEventProducer;

    @MockBean
    private PaymentGateway paymentGateway;

    private User user;
    private Cart cart;
    private Category category;

    @BeforeEach
    void setUp() {
        cleanDatabase();
        user = createUser("test@example.com");
        cart = createCart(user);
        category = createCategory("electronics");
    }

    @Test
    void shouldCheckoutSuccessfully() throws Exception {
        addCartItem(cart, "Laptop", new BigDecimal("1000.00"), 2);

        String responseBody = mockMvc.perform(post("/api/v1/orders/checkout")
                        .with(SecurityMockMvcRequestPostProcessors.user("test@example.com").roles("USER"))
                        .header("Idempotency-Key", "checkout-success-key"))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode response = objectMapper.readTree(responseBody);
        Long orderId = response.get("orderId").asLong();

        assertThat(orderId).isNotNull();
        assertThat(orderRepository.count()).isEqualTo(1L);
        assertThat(cartItemRepository.findAllByCartId(cart.getId())).isEmpty();

        IdempotencyRecord record = idempotencyRecordRepository.findByIdempotencyKey("checkout-success-key").orElseThrow();
        assertThat(record.getStatus()).isEqualTo(IdempotencyStatus.SUCCESS);
        assertThat(record.getResponseOrderId()).isEqualTo(orderId);
    }

    @Test
    void shouldFailCheckoutWhenCartIsEmpty() throws Exception {
        mockMvc.perform(post("/api/v1/orders/checkout")
                        .with(SecurityMockMvcRequestPostProcessors.user("test@example.com").roles("USER"))
                        .header("Idempotency-Key", "checkout-empty-cart-key"))
                .andExpect(status().isBadRequest());

        assertThat(orderRepository.count()).isEqualTo(0L);

        IdempotencyRecord record = idempotencyRecordRepository.findByIdempotencyKey("checkout-empty-cart-key").orElseThrow();
        assertThat(record.getStatus()).isEqualTo(IdempotencyStatus.FAILED);
    }

    private User createUser(String email) {
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setFirstName("Test");
        newUser.setLastName("User");
        newUser.setRole(Role.ROLE_USER);
        return userRepository.save(newUser);
    }

    private Cart createCart(User cartUser) {
        Cart newCart = new Cart();
        newCart.setUser(cartUser);
        return cartRepository.save(newCart);
    }

    private Category createCategory(String name) {
        Category newCategory = new Category();
        newCategory.setName(name);
        newCategory.setDescription("test-category");
        return categoryRepository.save(newCategory);
    }

    private void addCartItem(Cart targetCart, String productName, BigDecimal price, int quantity) {
        Product product = new Product();
        product.setName(productName);
        product.setDescription("test-product");
        product.setPrice(price);
        product.setStock(100);
        product.setCategory(category);
        product = productRepository.save(product);

        CartItem item = new CartItem();
        item.setCart(targetCart);
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setUnitPrice(price);
        cartItemRepository.save(item);
    }

    private void cleanDatabase() {
        idempotencyRecordRepository.deleteAll();
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        cartItemRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        cartRepository.deleteAll();
        userRepository.deleteAll();
    }
}
