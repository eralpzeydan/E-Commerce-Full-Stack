package com.eralp.ecommerce.repository;

import com.eralp.ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByName(String name);
    boolean existsByName(String name);
}
