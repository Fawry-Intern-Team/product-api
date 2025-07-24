package com.fawry.product_api.service;

import com.fawry.product_api.model.dto.ProductDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    ProductDto addProduct(@Valid ProductDto productDto);

    ProductDto getProductById(UUID id);

    ProductDto updateProduct(UUID id, @Valid ProductDto productDto);

    ProductDto deleteProduct(UUID id);

    Page<ProductDto> findByCategoryName(String categoryName, int page, int size);

    Page<ProductDto> getProductsByPriceRange(double minPrice, double maxPrice, int page, int size);

    Page<ProductDto> searchProducts(String keyword, int page, int size);

    List<String> getSearchSuggestions(String partial);

    Page<ProductDto> getAllProductsWithPagination(int page, int size);

    Page<ProductDto> getAllProductsSorted(String sortBy, String sortDirection, int page, int size);
}
