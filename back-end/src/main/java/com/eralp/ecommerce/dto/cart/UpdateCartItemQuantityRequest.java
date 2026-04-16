package com.eralp.ecommerce.dto.cart;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCartItemQuantityRequest {

    @NotNull(message = "Quantity must not be null")
    @PositiveOrZero(message = "Quantity must be zero or greater")
    private Integer quantity;
}
