package com.eralp.ecommerce.service.impl;

import com.eralp.ecommerce.dto.common.PagedResponse;
import com.eralp.ecommerce.dto.product.CreateProductRequest;
import com.eralp.ecommerce.dto.product.ProductResponse;
import com.eralp.ecommerce.dto.product.UpdateProductRequest;
import com.eralp.ecommerce.entity.Category;
import com.eralp.ecommerce.entity.Product;
import com.eralp.ecommerce.exception.ResourceNotFoundException;
import com.eralp.ecommerce.repository.CategoryRepository;
import com.eralp.ecommerce.repository.OrderItemRepository;
import com.eralp.ecommerce.repository.ProductRepository;
import com.eralp.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final OrderItemRepository orderItemRepository;

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
    public PagedResponse<ProductResponse> getAllProducts(
            int page,
            int size,
            String sortBy,
            String sortDir,
            String name,
            Long categoryId
    ) {
        validatePagination(page, size);
        validateSortBy(sortBy);
        validateSortDir(sortDir);

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Product> productPage;
        boolean hasName = name != null && !name.trim().isEmpty();
        boolean hasCategoryId = categoryId != null;

        if (hasName && hasCategoryId) {
            productPage = productRepository.findByNameContainingIgnoreCaseAndCategoryId(
                    name.trim(),
                    categoryId,
                    pageable
            );
        } else if (hasName) {
            productPage = productRepository.findByNameContainingIgnoreCase(name.trim(), pageable);
        } else if (hasCategoryId) {
            productPage = productRepository.findByCategoryId(categoryId, pageable);
        } else {
            productPage = productRepository.findAll(pageable);
        }

        return PagedResponse.<ProductResponse>builder()
                .content(productPage.getContent().stream().map(this::mapToResponse).toList())
                .page(productPage.getNumber())
                .size(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .first(productPage.isFirst())
                .last(productPage.isLast())
                .sortBy(sortBy)
                .sortDir(sortDir.toLowerCase())
                .build();
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
        if (orderItemRepository.existsByProductId(id)) {
            throw new IllegalStateException("Product cannot be deleted because it exists in orders");
        }
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

    private void validatePagination(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Page must be zero or greater");
        }
        if (size <= 0 || size > 100) {
            throw new IllegalArgumentException("Size must be between 1 and 100");
        }
    }

    private void validateSortBy(String sortBy) {
        if (!sortBy.equals("id")
                && !sortBy.equals("name")
                && !sortBy.equals("price")
                && !sortBy.equals("stock")) {
            throw new IllegalArgumentException("Invalid sortBy field");
        }
    }

    private void validateSortDir(String sortDir) {
        if (!sortDir.equalsIgnoreCase("asc") && !sortDir.equalsIgnoreCase("desc")) {
            throw new IllegalArgumentException("sortDir must be asc or desc");
        }
    }
}
