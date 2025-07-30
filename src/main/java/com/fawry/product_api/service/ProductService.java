package com.fawry.product_api.service;

import com.fawry.product_api.external.store.StoreProductResponse;
import com.fawry.product_api.model.dto.ProductDto;
import com.fawry.product_api.model.entity.Product;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    ProductDto addProduct(@Valid ProductDto productDto);

    ProductDto getProductById(UUID id);

    ProductDto updateProduct(UUID id, @Valid ProductDto productDto);

    ProductDto deleteProduct(UUID id);

    List<String> getSearchSuggestions(String partial);

    Page<StoreProductResponse> getFilteredProducts(String keyword, String category, double min, double max,
                                         String sortBy, String sortDirection, int page, int size);

    Page<StoreProductResponse> getAllProductsWithStore(List<Product> products, int page, int size);

    List<StoreProductResponse> fetchProductDetailsWithStore(List<UUID> productIds);


}
