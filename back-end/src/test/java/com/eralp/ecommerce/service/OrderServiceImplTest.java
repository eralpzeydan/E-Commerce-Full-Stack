package com.eralp.ecommerce.service;

import com.eralp.ecommerce.client.PaymentClient;
import com.eralp.ecommerce.dto.order.OrderResponse;
import com.eralp.ecommerce.entity.Cart;
import com.eralp.ecommerce.entity.CartItem;
import com.eralp.ecommerce.entity.Order;
import com.eralp.ecommerce.entity.Product;
import com.eralp.ecommerce.entity.User;
import com.eralp.ecommerce.exception.BadRequestException;
import com.eralp.ecommerce.exception.ResourceNotFoundException;
import com.eralp.ecommerce.messaging.OrderEventProducer;
import com.eralp.ecommerce.repository.CartItemRepository;
import com.eralp.ecommerce.repository.CartRepository;
import com.eralp.ecommerce.repository.OrderRepository;
import com.eralp.ecommerce.repository.UserRepository;
import com.eralp.ecommerce.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderEventProducer orderEventProducer;

    @Mock
    private IdempotencyService idempotencyService;

    @Mock
    private PaymentClient paymentClient;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void shouldCreateOrderWhenCartHasItems() {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@test.com");

        Cart cart = new Cart();
        cart.setId(10L);
        cart.setUser(user);

        Product product = new Product();
        product.setId(100L);
        product.setName("Laptop");

        CartItem cartItem = new CartItem();
        cartItem.setId(1000L);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
        cartItem.setUnitPrice(new BigDecimal("250.00"));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findAllByCartId(10L)).thenReturn(List.of(cartItem));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0, Order.class);
            order.setId(55L);
            return order;
        });

        OrderResponse response = orderService.createOrderFromCart(1L);

        assertThat(response.getOrderId()).isEqualTo(55L);
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getTotalAmount()).isEqualByComparingTo("500.00");
        assertThat(response.getItems()).hasSize(1);

        verify(paymentClient).authorizePayment(55L, new BigDecimal("500.00"), null);
        verify(cartItemRepository).deleteAll(List.of(cartItem));
        verify(cartItemRepository).flush();
    }

    @Test
    void shouldThrowWhenCartIsEmpty() {
        User user = new User();
        user.setId(2L);

        Cart cart = new Cart();
        cart.setId(22L);
        cart.setUser(user);

        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(2L)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findAllByCartId(22L)).thenReturn(List.of());

        assertThatThrownBy(() -> orderService.createOrderFromCart(2L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Cannot create order from empty cart");
    }

    @Test
    void shouldThrowWhenUserDoesNotExist() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.createOrderFromCart(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found with id: 999");
    }
}
