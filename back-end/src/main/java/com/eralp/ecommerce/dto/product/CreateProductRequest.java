package com.eralp.ecommerce.dto.product;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateProductRequest {

    @NotBlank(message = "Product name must not be blank")
    private String name;

    @NotNull(message = "Product name must not be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Product price must be greater than zero")
    private BigDecimal price;

    @NotNull(message = "Product stock must not be null")
    @PositiveOrZero(message = "Product stock must be zero or greater")
    private Integer stock;
}
