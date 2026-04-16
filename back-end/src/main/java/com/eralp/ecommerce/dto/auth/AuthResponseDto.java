package com.eralp.ecommerce.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponseDto {

    private Long userId;
    private String name;
    private String email;
    private String message;
}
