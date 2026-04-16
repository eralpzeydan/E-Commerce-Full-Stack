package com.eralp.ecommerce.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/")
    public ResponseEntity<String> root() {
        return ResponseEntity.ok("E-commerce back-end is running");
    }

    @GetMapping("api/v1/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("E-commerce back-end is running");
    }
}
