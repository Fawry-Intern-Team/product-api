package com.fawry.product_api.service;

import com.fawry.product_api.external.stock.Stock;
import com.fawry.product_api.external.store.Store;
import com.fawry.product_api.external.store.StoreClient;
import com.fawry.product_api.external.store.StoreProductResponse;
import com.fawry.product_api.mapper.ProductMapper;
import com.fawry.product_api.model.dto.ProductDto;
import com.fawry.product_api.model.entity.Category;
import com.fawry.product_api.model.entity.Product;
import com.fawry.product_api.repository.CategoryRepository;
import com.fawry.product_api.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    private final StoreClient storeClient;


    @Override
    public ProductDto addProduct(ProductDto productDto) {
        Category category = categoryRepository.findByName(productDto.getCategoryName())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Product product = productMapper.toEntity(productDto);
        product.setCategory(category);

        productRepository.save(product);
        log.info("Product added successfully: {}", productDto.getName());
        return productMapper.toDto(product);
    }

    @Override
    public ProductDto getProductById(UUID id) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        log.info("Product found: {}", existingProduct.getName());

        return productMapper.toDto(existingProduct);
    }

    @Override
    public ProductDto updateProduct(UUID id, ProductDto productDto) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        productMapper.updateProductFromDto(productDto, existingProduct);
        log.info("Updating product with id: {}", id);

        productRepository.save(existingProduct);
        log.info("Product updated successfully: {}", existingProduct.getName());

        return productMapper.toDto(existingProduct);
    }

    @Override
    public ProductDto deleteProduct(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        log.info("Deleting product with id: {}", id);
        product.setDeleted(true);
        productRepository.save(product);

        return productMapper.toDto(product);
    }


    @Override
    public List<String> getSearchSuggestions(String partial) {
        List<String> suggestions = productRepository.getSearchSuggestions(partial);
        if (suggestions.isEmpty()) {
            log.warn("No search suggestions found for partial: {}", partial);
        } else {
            log.info("Found {} search suggestions for partial: {}", suggestions.size(), partial);
        }
        return suggestions;
    }

    @Override
    public Page<StoreProductResponse> getFilteredProducts(String keyword, String category, double min, double max,
                                                          String sortBy, String sortDirection, int page, int size) {

        String tsQuery = (keyword == null || keyword.trim().isEmpty())
                ? "" : keyword.trim().replaceAll("[^\\w\\s]", "").replaceAll("\\s+", " & ");

        Pageable pageable = PageRequest.of(page, size);

        Page<Product> products = productRepository.searchFullText(
                tsQuery,
                category,
                min > 0 ? min : null,
                max > 0 ? max : null,
                sortBy,
                sortDirection,
                pageable
        );

        return getAllProductsWithStore(products.getContent(), page, size);
    }

    @Override
    public Page<StoreProductResponse> getAllProductsWithStore(List<Product> products,int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        if(products == null || products.isEmpty()) {
            products = productRepository.findAll();
        }

        List<StoreProductResponse> responses = fetchProductDetailsWithStore(
                products.stream()
                        .map(Product::getId)
                        .collect(Collectors.toList())
        );

        return new PageImpl<>(responses, pageable, responses.size());
    }


    @Override
    public List<StoreProductResponse> fetchProductDetailsWithStore(List<UUID> productIds) {
        List<List<Stock>> stocks = storeClient.getProductsWithStore(productIds);
        log.debug("Fetched stocks count: {}", stocks.size());

        return stocks.stream()
                .flatMap(List::stream)
                .map(this::buildStoreProductResponse)
                .collect(Collectors.toList());
    }

    private StoreProductResponse buildStoreProductResponse(Stock stock) {
        Product product = productRepository.findById(stock.getProductId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Product not found with id: " + stock.getProductId()));

        Store store = storeClient.getStoreById(stock.getStoreId());

        return toStoreProductResponse(product, store);
    }

    private StoreProductResponse toStoreProductResponse(Product product, Store store) {
        return StoreProductResponse.builder()
                .productId(product.getId())
                .productName(product.getName())
                .productDescription(product.getDescription())
                .imageUrl(product.getImageUrl())
                .price(product.getPrice())
                .categoryName(product.getCategory().getName())
                .storeId(store.getId())
                .storeName(store.getName())
                .storeLocation(store.getLocation())
                .build();
    }

}
