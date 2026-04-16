package com.eralp.ecommerce.service;

import com.eralp.ecommerce.dto.product.CreateProductRequest;
import com.eralp.ecommerce.dto.product.ProductResponse;
import com.eralp.ecommerce.dto.product.UpdateProductRequest;

import java.util.List;

public interface ProductService {

    ProductResponse createProduct(CreateProductRequest request);

    List<ProductResponse> getAllProducts();

    ProductResponse getProductById(Long id);

    ProductResponse updateProduct(Long id, UpdateProductRequest request);

    void deleteProduct(Long id);
}
