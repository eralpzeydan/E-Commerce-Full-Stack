package com.eralp.ecommerce.service.impl;

import com.eralp.ecommerce.dto.cart.AddCartItemRequest;
import com.eralp.ecommerce.dto.cart.CartItemResponse;
import com.eralp.ecommerce.dto.cart.CartResponse;
import com.eralp.ecommerce.dto.cart.UpdateCartItemQuantityRequest;
import com.eralp.ecommerce.entity.Cart;
import com.eralp.ecommerce.entity.CartItem;
import com.eralp.ecommerce.entity.Product;
import com.eralp.ecommerce.entity.User;
import com.eralp.ecommerce.exception.ResourceNotFoundException;
import com.eralp.ecommerce.repository.CartItemRepository;
import com.eralp.ecommerce.repository.CartRepository;
import com.eralp.ecommerce.repository.ProductRepository;
import com.eralp.ecommerce.repository.UserRepository;
import com.eralp.ecommerce.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public CartResponse getCartByAuthenticatedUser(String email) {
        User user = findUserByEmail(email);
        return getCartByUserId(user.getId());
    }

    @Override
    @Transactional
    public CartResponse addItemToCartForAuthenticatedUser(String email, AddCartItemRequest request) {
        User user = findUserByEmail(email);
        return addItemToCart(user.getId(), request);
    }

    @Override
    @Transactional
    public CartResponse updateCartItemQuantityForAuthenticatedUser(
            String email,
            Long cartItemId,
            UpdateCartItemQuantityRequest request
    ) {
        User user = findUserByEmail(email);
        return updateCartItemQuantity(user.getId(), cartItemId, request);
    }

    @Override
    @Transactional
    public void removeCartItemForAuthenticatedUser(String email, Long cartItemId) {
        User user = findUserByEmail(email);
        removeCartItem(user.getId(), cartItemId);
    }

    @Override
    @Transactional
    public CartResponse getCartByUserId(Long userId) {
        Cart cart = getOrCreateCart(userId);
        return mapToCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse addItemToCart(Long userId, AddCartItemRequest request) {
        Cart cart = getOrCreateCart(userId);
        Product product = findProductById(request.getProductId());

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                .orElseGet(() -> createCartItem(cart, product));

        cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
        cartItemRepository.save(cartItem);

        return mapToCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse updateCartItemQuantity(Long userId, Long cartItemId, UpdateCartItemQuantityRequest request) {
        Cart cart = getOrCreateCart(userId);

        CartItem cartItem = cartItemRepository.findByIdAndCartId(cartItemId, cart.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));

        if (request.getQuantity() == 0) {
            cartItemRepository.delete(cartItem);
            cartItemRepository.flush();
            return mapToCartResponse(cart);
        }

        cartItem.setQuantity(request.getQuantity());

        return mapToCartResponse(cart);
    }

    @Override
    @Transactional
    public void removeCartItem(Long userId, Long cartItemId) {
        Cart cart = getOrCreateCart(userId);

        CartItem cartItem = cartItemRepository.findByIdAndCartId(cartItemId, cart.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));

        cartItemRepository.delete(cartItem);
    }

    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

                    Cart cart = new Cart();
                    cart.setUser(user);
                    return cartRepository.save(cart);
                });
    }

    private User findUserByEmail(String email) {
        String normalizedEmail = email.trim().toLowerCase();
        return userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + normalizedEmail));
    }

    private Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
    }

    private CartItem createCartItem(Cart cart, Product product) {
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(0);
        cartItem.setUnitPrice(product.getPrice());
        return cartItem;
    }

    private CartResponse mapToCartResponse(Cart cart) {
        List<CartItem> items = cartItemRepository.findAllByCartId(cart.getId());

        List<CartItemResponse> itemResponses = items.stream()
                .map(this::mapToCartItemResponse)
                .toList();

        BigDecimal total = itemResponses.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .cartId(cart.getId())
                .userId(cart.getUser().getId())
                .items(itemResponses)
                .total(total)
                .build();
    }

    private CartItemResponse mapToCartItemResponse(CartItem cartItem) {
        BigDecimal subtotal = cartItem.getUnitPrice()
                .multiply(BigDecimal.valueOf(cartItem.getQuantity()));

        return CartItemResponse.builder()
                .cartItemId(cartItem.getId())
                .productId(cartItem.getProduct().getId())
                .productName(cartItem.getProduct().getName())
                .quantity(cartItem.getQuantity())
                .unitPrice(cartItem.getUnitPrice())
                .subtotal(subtotal)
                .build();
    }
}
