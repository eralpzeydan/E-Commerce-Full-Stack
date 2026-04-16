package com.eralp.ecommerce.service.impl;

import com.eralp.ecommerce.dto.user.CreateUserRequest;
import com.eralp.ecommerce.dto.user.UserResponse;
import com.eralp.ecommerce.entity.User;
import com.eralp.ecommerce.exception.DuplicateResourceException;
import com.eralp.ecommerce.exception.ResourceNotFoundException;
import com.eralp.ecommerce.repository.UserRepository;
import com.eralp.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

   private final UserRepository userRepository;

   @Override
   @Transactional
    public UserResponse createUser(CreateUserRequest request){
       String normalizedEmail = request.getEmail().trim().toLowerCase();

       if (userRepository.existsByEmail(normalizedEmail)) {
           throw new DuplicateResourceException("User with this email already exists");
       }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(normalizedEmail);

        User savedUser = userRepository.save(user);

        return mapToResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers(){
       return userRepository.findAll()
               .stream()
               .map(this::mapToResponse)
               .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id){
       User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
       return mapToResponse(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id){
        if (!userRepository.existsById(id)) {
           throw new ResourceNotFoundException("User not found with id: " + id);
       }
       userRepository.deleteById(id);
    }


    public UserResponse mapToResponse(User user){
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .build();
    }
}
