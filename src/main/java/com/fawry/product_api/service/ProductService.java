package com.fawry.product_api.service;

import com.fawry.product_api.model.dto.ProductDto;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    ProductDto addProduct(@Valid ProductDto productDto);

    ProductDto getProductById(UUID id);

    List<ProductDto> getAllProducts();

    ProductDto updateProduct(UUID id, @Valid ProductDto productDto);

    ProductDto deleteProduct(UUID id);

    List<ProductDto> findByCategoryName(String categoryName);

    List<ProductDto> getProductsByPriceRange(double minPrice, double maxPrice);

    List<ProductDto> searchProducts(String keyword);

    List<String> getSearchSuggestions(String partial);

    List<ProductDto> getAllProductsWithPagination(int page, int size);

    List<ProductDto> getAllProductsSorted(String sortBy, String sortDirection, int page, int size);
}
