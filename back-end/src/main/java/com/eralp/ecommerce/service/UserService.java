package com.eralp.ecommerce.service;

import com.eralp.ecommerce.dto.user.CreateUserRequest;
import com.eralp.ecommerce.dto.user.UserResponse;

import java.util.List;


public interface UserService {
    UserResponse createUser(CreateUserRequest request);
    List<UserResponse> getAllUsers();
    UserResponse getUserById(Long id);
    void deleteUser(Long id);
}
