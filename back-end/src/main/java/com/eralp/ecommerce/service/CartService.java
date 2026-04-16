package com.eralp.ecommerce.service;

import com.eralp.ecommerce.dto.cart.AddCartItemRequest;
import com.eralp.ecommerce.dto.cart.CartResponse;
import com.eralp.ecommerce.dto.cart.UpdateCartItemQuantityRequest;

public interface CartService {

    CartResponse getCartByUserId(Long userId);

    CartResponse addItemToCart(Long userId, AddCartItemRequest request);

    CartResponse updateCartItemQuantity(Long userId, Long cartItemId, UpdateCartItemQuantityRequest request);

    void removeCartItem(Long userId, Long cartItemId);
}
