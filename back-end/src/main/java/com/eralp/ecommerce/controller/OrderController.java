package com.eralp.ecommerce.controller;

import com.eralp.ecommerce.dto.order.OrderResponse;
import com.eralp.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout(
            Authentication authentication,
            @RequestHeader("Idempotency-Key") String idempotencyKey
    ) {
        String email = authentication.getName();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrderFromAuthenticatedUser(email, idempotencyKey));
    }

    @GetMapping("/secure-test")
    public ResponseEntity<String> secureTest() {
        return ResponseEntity.ok("You are authenticated");
    }
}
