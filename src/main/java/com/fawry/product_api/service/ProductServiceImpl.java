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
import com.fawry.product_api.util.ProductSpecifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    public Page<ProductDto> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.findAll(pageable);

        log.info("Total products found: {}", productPage.getTotalElements());

        return productPage.map(productMapper::toDto);
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
    public Page<ProductDto> getFilteredProducts(String keyword, String category, double min, double max,
                                                String sortBy, String sortDirection, int page, int size) {

        Specification<Product> specification = ProductSpecifications.withAllFilters(keyword, category, min, max);

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Product> productPage = productRepository.findAll(specification, pageable);
        log.info("Fetching filtered products with keyword: {}, category: {}, minPrice: {}, maxPrice: {}, sortBy: {}, sortDirection: {}, page: {}, size: {}",
                keyword, category, min, max, sortBy, sortDirection, page, size);

        return productPage.map(productMapper::toDto);
    }

    @Override
    public List<StoreProductResponse> fetchProductDetailsWithStore(List<UUID> productIds) {
        List<List<Stock>> stocks = storeClient.getProductsWithStore(productIds);
        log.info("Fetched stocks {}: {}", stocks.size(), stocks);
        List<StoreProductResponse> storeProductResponses = new ArrayList<>();
        stocks.forEach(stockList -> {
            stockList.forEach(stock -> {
                Product product = productRepository.findById(stock.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product not found with id: " + stock.getProductId()));
                Store store = storeClient.getStoreById(stock.getStoreId());
                StoreProductResponse response = getStoreProductResponse(product, store);
                storeProductResponses.add(response);
            });
        });

        return storeProductResponses;

    }

    private static StoreProductResponse getStoreProductResponse(Product product, Store store) {
        StoreProductResponse response = new StoreProductResponse();
        response.setProductId(product.getId());
        response.setProductName(product.getName());
        response.setProductDescription(product.getDescription());
        response.setImageUrl(product.getImageUrl());
        response.setPrice(product.getPrice());
        response.setStoreId(store.getId());
        response.setStoreName(store.getName());
        response.setStoreLocation(store.getLocation());
        return response;
    }


}
