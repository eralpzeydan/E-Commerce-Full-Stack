package com.eralp.ecommerce.dto.user;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {

    private long id;
    private String firstName;
    private String lastName;
    private String email;
}
