package com.eralp.ecommerce.controller;

import com.eralp.ecommerce.dto.cart.AddCartItemRequest;
import com.eralp.ecommerce.dto.cart.CartResponse;
import com.eralp.ecommerce.dto.cart.UpdateCartItemQuantityRequest;
import com.eralp.ecommerce.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users/{userId}/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getCartByUserId(userId));
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItemToCart(
            @PathVariable Long userId,
            @Valid @RequestBody AddCartItemRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cartService.addItemToCart(userId, request));
    }

    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponse> updateCartItemQuantity(
            @PathVariable Long userId,
            @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateCartItemQuantityRequest request
    ) {
        return ResponseEntity.ok(cartService.updateCartItemQuantity(userId, cartItemId, request));
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<Void> removeCartItem(
            @PathVariable Long userId,
            @PathVariable Long cartItemId
    ) {
        cartService.removeCartItem(userId, cartItemId);
        return ResponseEntity.noContent().build();
    }
}
