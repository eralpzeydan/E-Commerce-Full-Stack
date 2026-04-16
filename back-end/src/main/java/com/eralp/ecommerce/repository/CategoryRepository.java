package com.eralp.ecommerce.repository;

import com.eralp.ecommerce.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByName(String name);
}
