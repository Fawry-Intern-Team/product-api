package com.fawry.product_api.service;

import com.fawry.product_api.model.dto.ProductDto;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    ProductDto addProduct(ProductDto productDto);

    ProductDto getProductById(UUID id);

    List<ProductDto> getAllProducts();

    ProductDto updateProduct(UUID id, ProductDto productDto);

    ProductDto deleteProduct(UUID id);

    List<ProductDto> findByCategoryName(String categoryName);

    List<ProductDto> getProductsByPriceRange(double minPrice, double maxPrice);
}
