package com.eralp.ecommerce.service;

import com.eralp.ecommerce.dto.auth.AuthResponseDto;
import com.eralp.ecommerce.dto.auth.LoginRequestDto;
import com.eralp.ecommerce.dto.auth.RegisterRequestDto;

public interface AuthService {

    AuthResponseDto register(RegisterRequestDto request);

    AuthResponseDto login(LoginRequestDto request);
}
