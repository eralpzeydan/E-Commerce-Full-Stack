package com.eralp.ecommerce.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    private static final String SERVICE_RUNNING_MESSAGE = "E-commerce back-end is running";

    @GetMapping("/")
    public ResponseEntity<String> root() {
        return ResponseEntity.ok(SERVICE_RUNNING_MESSAGE);
    }

    @GetMapping("/api/v1/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok(SERVICE_RUNNING_MESSAGE);
    }
}
