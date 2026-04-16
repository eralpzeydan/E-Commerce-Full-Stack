package com.eralp.ecommerce.service;

import com.eralp.ecommerce.dto.category.CategoryResponse;
import com.eralp.ecommerce.dto.category.CreateCategoryRequest;
import com.eralp.ecommerce.dto.category.UpdateCategoryRequest;

import java.util.List;

public interface CategoryService {

    CategoryResponse createCategory(CreateCategoryRequest request);

    List<CategoryResponse> getAllCategories();

    CategoryResponse getCategoryById(Long id);

    CategoryResponse updateCategory(Long id, UpdateCategoryRequest request);

    void deleteCategory(Long id);
}
