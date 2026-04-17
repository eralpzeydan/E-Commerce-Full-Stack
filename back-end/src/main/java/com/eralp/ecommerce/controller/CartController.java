package com.eralp.ecommerce.controller;

import com.eralp.ecommerce.dto.cart.AddCartItemRequest;
import com.eralp.ecommerce.dto.cart.CartResponse;
import com.eralp.ecommerce.dto.cart.UpdateCartItemQuantityRequest;
import com.eralp.ecommerce.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart(Authentication authentication) {
        return ResponseEntity.ok(cartService.getCartByAuthenticatedUser(authentication.getName()));
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItemToCart(
            Authentication authentication,
            @Valid @RequestBody AddCartItemRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cartService.addItemToCartForAuthenticatedUser(authentication.getName(), request));
    }

    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponse> updateCartItemQuantity(
            Authentication authentication,
            @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateCartItemQuantityRequest request
    ) {
        return ResponseEntity.ok(
                cartService.updateCartItemQuantityForAuthenticatedUser(authentication.getName(), cartItemId, request)
        );
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<Void> removeCartItem(
            Authentication authentication,
            @PathVariable Long cartItemId
    ) {
        cartService.removeCartItemForAuthenticatedUser(authentication.getName(), cartItemId);
        return ResponseEntity.noContent().build();
    }
}
