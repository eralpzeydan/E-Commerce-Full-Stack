package com.eralp.ecommerce.service.impl;

import com.eralp.ecommerce.dto.user.CreateUserRequest;
import com.eralp.ecommerce.dto.user.UserResponse;
import com.eralp.ecommerce.entity.User;
import com.eralp.ecommerce.exception.ConflictException;
import com.eralp.ecommerce.exception.DuplicateResourceException;
import com.eralp.ecommerce.exception.ResourceNotFoundException;
import com.eralp.ecommerce.repository.CartRepository;
import com.eralp.ecommerce.repository.OrderRepository;
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
   private final CartRepository cartRepository;
   private final OrderRepository orderRepository;

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
       User user = findUserById(id);
       if (orderRepository.existsByUserId(id)) {
           throw new ConflictException("User cannot be deleted because they have orders");
       }
       cartRepository.findByUserId(id).ifPresent(cartRepository::delete);
       userRepository.delete(user);
    }

    private User findUserById(Long id) {
       return userRepository.findById(id)
               .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private UserResponse mapToResponse(User user){
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .build();
    }
}
