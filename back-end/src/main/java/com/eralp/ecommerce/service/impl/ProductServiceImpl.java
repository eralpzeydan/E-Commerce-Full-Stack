package com.eralp.ecommerce.service.impl;

import com.eralp.ecommerce.entity.Product;
import com.eralp.ecommerce.exception.DuplicateResourceException;
import com.eralp.ecommerce.dto.product.CreateProductRequest;
import com.eralp.ecommerce.dto.product.ProductResponse;
import com.eralp.ecommerce.repository.ProductRepository;
import com.eralp.ecommerce.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    @Transactional
    public ProductResponse createProduct(CreateProductRequest request){
        String normalisedName = request.getName().trim();

        if(productRepository.existsByName(normalisedName)){
            throw new DuplicateResourceException("Product with this name already exist");
        }

        Product product = new Product();
        product.setName(normalisedName);
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());

        Product savedProduct = productRepository.save(product);

        return ProductResponse.builder()
                .id(savedProduct.getId())
                .name(savedProduct.getName())
                .price(savedProduct.getPrice())
                .stock(savedProduct.getStock())
                .build();

    }
}
