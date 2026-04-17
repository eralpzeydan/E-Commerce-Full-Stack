package com.eralp.ecommerce.service.impl;

import com.eralp.ecommerce.client.PaymentClient;
import com.eralp.ecommerce.dto.event.OrderCreatedEvent;
import com.eralp.ecommerce.dto.order.OrderItemResponse;
import com.eralp.ecommerce.dto.order.OrderResponse;
import com.eralp.ecommerce.entity.Cart;
import com.eralp.ecommerce.entity.CartItem;
import com.eralp.ecommerce.entity.IdempotencyOperationType;
import com.eralp.ecommerce.entity.IdempotencyRecord;
import com.eralp.ecommerce.entity.IdempotencyStatus;
import com.eralp.ecommerce.entity.Order;
import com.eralp.ecommerce.entity.OrderItem;
import com.eralp.ecommerce.entity.OrderStatus;
import com.eralp.ecommerce.entity.Product;
import com.eralp.ecommerce.entity.User;
import com.eralp.ecommerce.exception.BadRequestException;
import com.eralp.ecommerce.exception.ConflictException;
import com.eralp.ecommerce.exception.ResourceNotFoundException;
import com.eralp.ecommerce.messaging.OrderEventProducer;
import com.eralp.ecommerce.repository.CartItemRepository;
import com.eralp.ecommerce.repository.CartRepository;
import com.eralp.ecommerce.repository.OrderRepository;
import com.eralp.ecommerce.repository.UserRepository;
import com.eralp.ecommerce.service.IdempotencyService;
import com.eralp.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigInteger;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final OrderEventProducer orderEventProducer;
    private final IdempotencyService idempotencyService;
    private final PaymentClient paymentClient;

    @Override
    @Transactional
    public OrderResponse createOrderFromAuthenticatedUser(String email, String idempotencyKey) {
        String normalizedKey = normalizeIdempotencyKey(idempotencyKey);
        User user = findUserByEmail(email);
        String requestHash = buildCheckoutRequestHash(user);

        IdempotencyRecord existingRecord = idempotencyService.findByKey(normalizedKey).orElse(null);
        if (existingRecord != null) {
            return handleExistingIdempotencyRecord(existingRecord, user, requestHash);
        }

        IdempotencyRecord record;
        try {
            record = idempotencyService.createProcessingRecord(
                    normalizedKey,
                    user.getId(),
                    IdempotencyOperationType.CHECKOUT,
                    requestHash
            );
        } catch (DataIntegrityViolationException ex) {
            IdempotencyRecord conflictingRecord = idempotencyService.findByKey(normalizedKey)
                    .orElseThrow(() -> new ConflictException("Unable to acquire idempotency lock for checkout"));
            return handleExistingIdempotencyRecord(conflictingRecord, user, requestHash);
        }

        try {
            OrderResponse response = createOrderForUser(user, normalizedKey);
            idempotencyService.markSuccess(record.getId(), response.getOrderId());
            return response;
        } catch (Exception ex) {
            idempotencyService.markFailed(record.getId());
            throw ex;
        }
    }

    @Override
    @Transactional
    public OrderResponse createOrderFromCart(Long userId) {
        User user = findUserById(userId);
        return createOrderForUser(user, null);
    }

    private OrderResponse handleExistingIdempotencyRecord(
            IdempotencyRecord record,
            User user,
            String requestHash
    ) {
        validateIdempotencyOwnership(record, user.getId());

        if (record.getStatus() == IdempotencyStatus.SUCCESS) {
            Order existingOrder = getSuccessfulOrder(record);
            return mapToResponse(existingOrder);
        }

        if (record.getStatus() == IdempotencyStatus.PROCESSING) {
            validateProcessingRequestHash(record, requestHash);
            throw new ConflictException("Request is already being processed");
        }

        throw new ConflictException("Previous request failed. Please retry with a new Idempotency-Key");
    }

    private void validateIdempotencyOwnership(IdempotencyRecord record, Long userId) {
        if (!record.getUserId().equals(userId)) {
            throw new ConflictException("Idempotency key belongs to another user");
        }
        if (record.getOperationType() != IdempotencyOperationType.CHECKOUT) {
            throw new ConflictException("Idempotency key is reserved for another operation");
        }
    }

    private void validateProcessingRequestHash(IdempotencyRecord record, String requestHash) {
        String existingRequestHash = record.getRequestHash();
        if (existingRequestHash != null && !existingRequestHash.equals(requestHash)) {
            throw new ConflictException("Same idempotency key cannot be used with different payload");
        }
    }

    private String normalizeIdempotencyKey(String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new BadRequestException("Idempotency-Key header is required");
        }
        return idempotencyKey.trim();
    }

    private String buildCheckoutRequestHash(User user) {
        Cart cart = findCartByUserId(user.getId());
        List<CartItem> cartItems = cartItemRepository.findAllByCartId(cart.getId());
        String cartSnapshot = cartItems.stream()
                .sorted(Comparator.comparing(cartItem -> cartItem.getProduct().getId()))
                .map(cartItem -> cartItem.getProduct().getId() + "-" + cartItem.getQuantity())
                .collect(Collectors.joining(","));

        String hashInput = user.getId() + ":" + cartSnapshot;

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(hashInput.getBytes(StandardCharsets.UTF_8));
            return String.format("%064x", new BigInteger(1, hashedBytes));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("Unable to generate request hash");
        }
    }

    private OrderResponse createOrderForUser(User user, String idempotencyKey) {
        Cart cart = findCartByUserId(user.getId());
        List<CartItem> cartItems = getCartItemsForCheckout(cart);
        Order savedOrder = saveOrder(user, cartItems);

        paymentClient.authorizePayment(savedOrder.getId(), savedOrder.getTotalAmount(), idempotencyKey);
        clearCartItems(cartItems);
        publishOrderCreatedAfterCommit(savedOrder);

        return mapToResponse(savedOrder);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    private User findUserByEmail(String email) {
        String normalizedEmail = email.trim().toLowerCase();
        return userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + normalizedEmail));
    }

    private Cart findCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user id: " + userId));
    }

    private List<CartItem> getCartItemsForCheckout(Cart cart) {
        List<CartItem> cartItems = cartItemRepository.findAllByCartId(cart.getId());
        if (cartItems.isEmpty()) {
            throw new BadRequestException("Cannot create order from empty cart");
        }
        return cartItems;
    }

    private Order saveOrder(User user, List<CartItem> cartItems) {
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = mapCartItemToOrderItem(cartItem);
            order.addOrderItem(orderItem);
            totalAmount = totalAmount.add(orderItem.getLineTotal());
        }

        order.setTotalAmount(totalAmount);
        return orderRepository.save(order);
    }

    private OrderItem mapCartItemToOrderItem(CartItem cartItem) {
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
        return orderItem;
    }

    private void clearCartItems(List<CartItem> cartItems) {
        cartItemRepository.deleteAll(cartItems);
        cartItemRepository.flush();
    }

    private Order getSuccessfulOrder(IdempotencyRecord record) {
        if (record.getResponseOrderId() == null) {
            throw new ConflictException("Idempotency record is successful but order reference is missing");
        }
        Long orderId = record.getResponseOrderId();
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
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
