package com.eralp.ecommerce.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCategoryRequest {

    @NotBlank(message = "Category name must not be blank")
    @Size(max = 100, message = "Category name must be at most 100 characters")
    private String name;

    @Size(max = 500, message = "Category description must be at most 500 characters")
    private String description;
}
