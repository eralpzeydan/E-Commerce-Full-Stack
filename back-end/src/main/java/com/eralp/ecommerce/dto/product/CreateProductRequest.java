package com.eralp.ecommerce.dto.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateProductRequest {

    @NotBlank(message = "Product name must not be blank")
    @Size(max = 150, message = "Product name must be at most 150 characters")
    private String name;

    @Size(max = 1000, message = "Product description must be at most 1000 characters")
    private String description;

    @NotNull(message = "Product price must not be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Product price must be greater than zero")
    private BigDecimal price;

    @NotNull(message = "Product stock must not be null")
    @PositiveOrZero(message = "Product stock must be zero or greater")
    private Integer stock;

    @NotNull(message = "Category id must not be null")
    private Long categoryId;
}
