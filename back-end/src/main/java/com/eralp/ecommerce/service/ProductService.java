package com.eralp.ecommerce.service;

import com.eralp.ecommerce.dto.product.CreateProductRequest;
import com.eralp.ecommerce.dto.product.ProductResponse;

public interface ProductService {

    ProductResponse createProduct(CreateProductRequest request);
}
