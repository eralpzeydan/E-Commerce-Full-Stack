package com.eralp.ecommerce.service;

import com.eralp.ecommerce.dto.order.OrderResponse;

public interface OrderService {

    OrderResponse createOrderFromAuthenticatedUser(String email);

    OrderResponse createOrderFromCart(Long userId);
}
