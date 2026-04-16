package com.eralp.ecommerce.service;

import com.eralp.ecommerce.dto.common.PagedResponse;
import com.eralp.ecommerce.dto.product.CreateProductRequest;
import com.eralp.ecommerce.dto.product.ProductResponse;
import com.eralp.ecommerce.dto.product.UpdateProductRequest;

public interface ProductService {

    ProductResponse createProduct(CreateProductRequest request);

    PagedResponse<ProductResponse> getAllProducts(
            int page,
            int size,
            String sortBy,
            String sortDir,
            String name,
            Long categoryId
    );

    ProductResponse getProductById(Long id);

    ProductResponse updateProduct(Long id, UpdateProductRequest request);

    void deleteProduct(Long id);


}
