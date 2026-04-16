package com.eralp.ecommerce.service.impl;

import com.eralp.ecommerce.dto.product.CreateProductRequest;
import com.eralp.ecommerce.dto.product.ProductResponse;
import com.eralp.ecommerce.dto.product.UpdateProductRequest;
import com.eralp.ecommerce.entity.Category;
import com.eralp.ecommerce.entity.Product;
import com.eralp.ecommerce.exception.ResourceNotFoundException;
import com.eralp.ecommerce.repository.CategoryRepository;
import com.eralp.ecommerce.repository.ProductRepository;
import com.eralp.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        Category category = findCategoryById(request.getCategoryId());

        Product product = new Product();
        product.setName(request.getName().trim());
        product.setDescription(normalizeNullable(request.getDescription()));
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);
        return mapToResponse(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = findProductById(id);
        return mapToResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, UpdateProductRequest request) {
        Product product = findProductById(id);
        Category category = findCategoryById(request.getCategoryId());

        product.setName(request.getName().trim());
        product.setDescription(normalizeNullable(request.getDescription()));
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategory(category);

        return mapToResponse(product);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = findProductById(id);
        productRepository.delete(product);
    }

    private Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    private Category findCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
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
