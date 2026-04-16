package com.eralp.ecommerce.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequest {

    @NotBlank(message = "First name can not be blank")
    @Size(max = 100, message = "First name can not be longer than 100 characters")
    private String firstName;

    @NotBlank(message = "Lasr name can not be blank")
    @Size(max = 100, message = "Lasr name can not be longer than 100 characters")
    private String lastName;

    @NotBlank(message = "Email name can not be blank")
    @Size(max = 225, message = "Email name can not be longer than 225 characters")
    private String email;
}
