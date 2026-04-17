package com.eralp.ecommerce.service.impl;

import com.eralp.ecommerce.dto.category.CategoryResponse;
import com.eralp.ecommerce.dto.category.CreateCategoryRequest;
import com.eralp.ecommerce.dto.category.UpdateCategoryRequest;
import com.eralp.ecommerce.entity.Category;
import com.eralp.ecommerce.exception.ConflictException;
import com.eralp.ecommerce.exception.DuplicateResourceException;
import com.eralp.ecommerce.exception.ResourceNotFoundException;
import com.eralp.ecommerce.repository.CategoryRepository;
import com.eralp.ecommerce.repository.ProductRepository;
import com.eralp.ecommerce.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        String normalizedName = request.getName().trim();

        if (categoryRepository.existsByName(normalizedName)) {
            throw new DuplicateResourceException("Category with this name already exists");
        }

        Category category = new Category();
        category.setName(normalizedName);
        category.setDescription(normalizeNullable(request.getDescription()));

        Category savedCategory = categoryRepository.save(category);
        return mapToResponse(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        Category category = findCategoryById(id);
        return mapToResponse(category);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long id, UpdateCategoryRequest request) {
        Category category = findCategoryById(id);
        String normalizedName = request.getName().trim();

        if (!category.getName().equals(normalizedName) && categoryRepository.existsByName(normalizedName)) {
            throw new DuplicateResourceException("Category with this name already exists");
        }

        category.setName(normalizedName);
        category.setDescription(normalizeNullable(request.getDescription()));

        return mapToResponse(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = findCategoryById(id);

        if (productRepository.existsByCategoryId(id)) {
            throw new ConflictException("Category cannot be deleted because it still has products");
        }

        categoryRepository.delete(category);
    }

    private Category findCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }

    private String normalizeNullable(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
