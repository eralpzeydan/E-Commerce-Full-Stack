package com.eralp.ecommerce.repository;

import com.eralp.ecommerce.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

    boolean existsByUserId(Long userId);
}
