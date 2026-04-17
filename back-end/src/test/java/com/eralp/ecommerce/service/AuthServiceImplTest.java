package com.eralp.ecommerce.service;

import com.eralp.ecommerce.dto.auth.AuthResponseDto;
import com.eralp.ecommerce.dto.auth.LoginRequestDto;
import com.eralp.ecommerce.dto.auth.RegisterRequestDto;
import com.eralp.ecommerce.entity.Role;
import com.eralp.ecommerce.entity.User;
import com.eralp.ecommerce.exception.BadRequestException;
import com.eralp.ecommerce.exception.UnauthorizedException;
import com.eralp.ecommerce.repository.UserRepository;
import com.eralp.ecommerce.security.JwtService;
import com.eralp.ecommerce.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void shouldRegisterUserSuccessfully() {
        RegisterRequestDto request = new RegisterRequestDto();
        request.setName("Eralp Zeydan");
        request.setEmail("ERALP@TEST.COM");
        request.setPassword("Password123");

        when(userRepository.existsByEmail("eralp@test.com")).thenReturn(false);
        when(passwordEncoder.encode("Password123")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0, User.class);
            user.setId(1L);
            return user;
        });

        AuthResponseDto response = authService.register(request);

        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getEmail()).isEqualTo("eralp@test.com");
        assertThat(response.getName()).isEqualTo("Eralp Zeydan");
        assertThat(response.getToken()).isNull();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getRole()).isEqualTo(Role.ROLE_USER);
        assertThat(savedUser.getPassword()).isEqualTo("encoded-password");
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        RegisterRequestDto request = new RegisterRequestDto();
        request.setName("Eralp");
        request.setEmail("eralp@test.com");
        request.setPassword("Password123");

        when(userRepository.existsByEmail("eralp@test.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Email is already in use");
    }

    @Test
    void shouldLoginSuccessfullyWithValidCredentials() {
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("eralp@test.com");
        request.setPassword("Password123");

        User user = new User();
        user.setId(5L);
        user.setFirstName("Eralp");
        user.setLastName("Zeydan");
        user.setEmail("eralp@test.com");
        user.setPassword("encoded-password");

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("eralp@test.com")
                .password("encoded-password")
                .authorities("ROLE_USER")
                .build();

        when(userRepository.findByEmail("eralp@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Password123", "encoded-password")).thenReturn(true);
        when(userDetailsService.loadUserByUsername("eralp@test.com")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("jwt-token");

        AuthResponseDto response = authService.login(request);

        assertThat(response.getUserId()).isEqualTo(5L);
        assertThat(response.getEmail()).isEqualTo("eralp@test.com");
        assertThat(response.getToken()).isEqualTo("jwt-token");
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsInvalid() {
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("eralp@test.com");
        request.setPassword("wrong-password");

        User user = new User();
        user.setEmail("eralp@test.com");
        user.setPassword("encoded-password");

        when(userRepository.findByEmail("eralp@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "encoded-password")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Invalid email or password");
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExistAtLogin() {
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("notfound@test.com");
        request.setPassword("Password123");

        when(userRepository.findByEmail("notfound@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Invalid email or password");
    }
}
