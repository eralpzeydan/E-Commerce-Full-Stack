package com.eralp.ecommerce.service;

import com.eralp.ecommerce.dto.cart.AddCartItemRequest;
import com.eralp.ecommerce.dto.cart.CartResponse;
import com.eralp.ecommerce.dto.cart.UpdateCartItemQuantityRequest;

public interface CartService {

    CartResponse getCartByAuthenticatedUser(String email);

    CartResponse addItemToCartForAuthenticatedUser(String email, AddCartItemRequest request);

    CartResponse updateCartItemQuantityForAuthenticatedUser(
            String email,
            Long cartItemId,
            UpdateCartItemQuantityRequest request
    );

    void removeCartItemForAuthenticatedUser(String email, Long cartItemId);

    CartResponse getCartByUserId(Long userId);

    CartResponse addItemToCart(Long userId, AddCartItemRequest request);

    CartResponse updateCartItemQuantity(Long userId, Long cartItemId, UpdateCartItemQuantityRequest request);

    void removeCartItem(Long userId, Long cartItemId);
}
