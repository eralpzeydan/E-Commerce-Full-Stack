package com.eralp.ecommerce.service.impl;

import com.eralp.ecommerce.dto.auth.AuthResponseDto;
import com.eralp.ecommerce.dto.auth.LoginRequestDto;
import com.eralp.ecommerce.dto.auth.RegisterRequestDto;
import com.eralp.ecommerce.entity.User;
import com.eralp.ecommerce.exception.BadRequestException;
import com.eralp.ecommerce.exception.UnauthorizedException;
import com.eralp.ecommerce.repository.UserRepository;
import com.eralp.ecommerce.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public AuthResponseDto register(RegisterRequestDto request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new BadRequestException("Email is already in use");
        }

        String normalizedName = request.getName().trim();
        String[] nameParts = normalizedName.split("\\s+", 2);

        User user = new User();
        user.setFirstName(nameParts[0]);
        user.setLastName(nameParts.length > 1 ? nameParts[1] : "");
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);

        return AuthResponseDto.builder()
                .userId(savedUser.getId())
                .name(toFullName(savedUser))
                .email(savedUser.getEmail())
                .message("User registered successfully")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponseDto login(LoginRequestDto request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (user.getPassword() == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        return AuthResponseDto.builder()
                .userId(user.getId())
                .name(toFullName(user))
                .email(user.getEmail())
                .message("Login successful")
                .build();
    }

    private String toFullName(User user) {
        String firstName = user.getFirstName() == null ? "" : user.getFirstName().trim();
        String lastName = user.getLastName() == null ? "" : user.getLastName().trim();
        return (firstName + " " + lastName).trim();
    }
}
