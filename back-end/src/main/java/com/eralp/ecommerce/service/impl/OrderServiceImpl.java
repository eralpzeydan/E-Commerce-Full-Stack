package com.eralp.ecommerce.service.impl;

import com.eralp.ecommerce.dto.event.OrderCreatedEvent;
import com.eralp.ecommerce.dto.order.OrderItemResponse;
import com.eralp.ecommerce.dto.order.OrderResponse;
import com.eralp.ecommerce.entity.Cart;
import com.eralp.ecommerce.entity.CartItem;
import com.eralp.ecommerce.entity.Order;
import com.eralp.ecommerce.entity.OrderItem;
import com.eralp.ecommerce.entity.OrderStatus;
import com.eralp.ecommerce.entity.Product;
import com.eralp.ecommerce.entity.User;
import com.eralp.ecommerce.exception.BadRequestException;
import com.eralp.ecommerce.exception.ResourceNotFoundException;
import com.eralp.ecommerce.messaging.OrderEventProducer;
import com.eralp.ecommerce.repository.CartItemRepository;
import com.eralp.ecommerce.repository.CartRepository;
import com.eralp.ecommerce.repository.OrderRepository;
import com.eralp.ecommerce.repository.UserRepository;
import com.eralp.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final OrderEventProducer orderEventProducer;

    @Override
    @Transactional
    public OrderResponse createOrderFromAuthenticatedUser(String email) {
        User user = findUserByEmail(email);
        return createOrderForUser(user);
    }

    @Override
    @Transactional
    public OrderResponse createOrderFromCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return createOrderForUser(user);
    }

    private OrderResponse createOrderForUser(User user) {
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user id: " + user.getId()));

        List<CartItem> cartItems = cartItemRepository.findAllByCartId(cart.getId());
        if (cartItems.isEmpty()) {
            throw new BadRequestException("Cannot create order from empty cart");
        }

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (product == null) {
                throw new BadRequestException("Cart contains invalid product");
            }
            if (cartItem.getQuantity() == null || cartItem.getQuantity() <= 0) {
                throw new BadRequestException("Cart item quantity must be greater than zero");
            }

            BigDecimal unitPrice = cartItem.getUnitPrice();
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(unitPrice);
            orderItem.setLineTotal(lineTotal);

            order.addOrderItem(orderItem);
            totalAmount = totalAmount.add(lineTotal);
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);

        cartItemRepository.deleteAll(cartItems);
        cartItemRepository.flush();

        publishOrderCreatedAfterCommit(savedOrder);

        return mapToResponse(savedOrder);
    }

    private User findUserByEmail(String email) {
        String normalizedEmail = email.trim().toLowerCase();
        return userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + normalizedEmail));
    }

    private void publishOrderCreatedAfterCommit(Order savedOrder) {
        OrderCreatedEvent event = new OrderCreatedEvent(
                savedOrder.getId(),
                savedOrder.getUser().getId(),
                savedOrder.getUser().getEmail(),
                savedOrder.getTotalAmount(),
                savedOrder.getCreatedAt()
        );

        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            publishOrderCreatedSafely(event);
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                publishOrderCreatedSafely(event);
            }
        });
    }

    private void publishOrderCreatedSafely(OrderCreatedEvent event) {
        try {
            orderEventProducer.publishOrderCreated(event);
        } catch (Exception ex) {
            log.warn("Failed to publish order created event. orderId={}", event.getOrderId(), ex);
        }
    }

    private OrderResponse mapToResponse(Order order) {
        List<OrderItemResponse> items = order.getOrderItems()
                .stream()
                .map(this::mapToItemResponse)
                .toList();

        return OrderResponse.builder()
                .orderId(order.getId())
                .userId(order.getUser().getId())
                .status(order.getStatus().name())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .items(items)
                .build();
    }

    private OrderItemResponse mapToItemResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .lineTotal(item.getLineTotal())
                .build();
    }
}
