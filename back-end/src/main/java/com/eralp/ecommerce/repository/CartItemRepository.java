package com.eralp.ecommerce.repository;

import com.eralp.ecommerce.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);

    Optional<CartItem> findByIdAndCartId(Long cartItemId, Long cartId);

    List<CartItem> findAllByCartId(Long cartId);

    void deleteAllByCartId(Long cartId);
}
