package com.eralp.ecommerce.dto.cart;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class CartResponse {

    private Long cartId;
    private Long userId;
    private List<CartItemResponse> items;
    private BigDecimal total;
}
