package com.fawry.product_api.service;

import com.fawry.product_api.mapper.CategoryMapper;
import com.fawry.product_api.mapper.ProductMapper;
import com.fawry.product_api.model.dto.ProductDto;
import com.fawry.product_api.model.entity.Category;
import com.fawry.product_api.model.entity.Product;
import com.fawry.product_api.repository.CategoryRepository;
import com.fawry.product_api.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository,
                              ProductMapper productMapper, CategoryMapper categoryMapper,
                              CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.categoryMapper = categoryMapper;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public ProductDto addProduct(ProductDto productDto) {
        log.info("Adding product to database: {}", productDto);
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
        log.info("Fetching product from database for id: {}", id);
        Product existingProduct = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        log.info("Product found: {}", existingProduct.getName());

        return productMapper.toDto(existingProduct);
    }


    @Override
    public ProductDto updateProduct(UUID id, ProductDto productDto) {
        log.info("Fetching product for update with id: {}", id);
        Product existingProduct = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        productMapper.updateProductFromDto(productDto, existingProduct);
        log.info("Updating product with id: {}", id);

        productRepository.save(existingProduct);
        log.info("Product updated successfully: {}", existingProduct.getName());

        return productMapper.toDto(existingProduct);

    }

    @Override
    public ProductDto deleteProduct(UUID id) {
        log.info("Fetching product for deletion with id: {}", id);
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        log.info("Deleting product with id: {}", id);
        productRepository.delete(product);
        log.info("Product deleted successfully: {}", product.getName());

        return productMapper.toDto(product);

    }

    @Override
    public Page<ProductDto> findByCategoryName(String categoryName, int page, int size) {
        log.info("Fetching products by category name: {}", categoryName);
        Category category = categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new RuntimeException("Category not found with name: " + categoryName));

        Page<Product> productsPage =
                productRepository.findByCategoryName(category.getName(), PageRequest.of(page, size));
        log.info("Total products found in category '{}': {}", categoryName, productsPage.getTotalElements());

        return productsPage.map(productMapper::toDto);
    }

    @Override
    public Page<ProductDto> getProductsByPriceRange(double minPrice, double maxPrice, int page, int size) {
        log.info("Fetching products by price range: {} - {}", minPrice, maxPrice);
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productsPage = productRepository.findByPriceBetween(minPrice, maxPrice, pageable);

        log.info("Total products found in price range {} - {}: {}", minPrice, maxPrice, productsPage.getTotalElements());

        return productsPage.map(productMapper::toDto);
    }

    @Override
    public Page<ProductDto> searchProducts(String keyword,int page, int size) {
        log.info("Searching products with keyword: {}", keyword);
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productsPage = productRepository.searchProducts(keyword, pageable);

        log.info("Total products found for keyword '{}': {}", keyword, productsPage.getTotalElements());

        return productsPage.map(productMapper::toDto);
    }

    @Override
    public List<String> getSearchSuggestions(String partial) {
        log.info("Fetching search suggestions for partial: {}", partial);
        List<String> suggestions = productRepository.getSearchSuggestions(partial);
        if (suggestions.isEmpty()) {
            log.warn("No search suggestions found for partial: {}", partial);
        } else {
            log.info("Found {} search suggestions for partial: {}", suggestions.size(), partial);
        }
        return suggestions;
    }

    @Override
    public Page<ProductDto> getAllProductsWithPagination(int page, int size) {
        log.info("Fetching all products with pagination, page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.findAll(pageable);

        log.info("Total products found: {}", productPage.getTotalElements());

        return productPage.map(productMapper::toDto);
    }

    @Override
    public Page<ProductDto> getAllProductsSorted(String sortBy, String sortDirection, int page, int size) {
        log.info("Fetching all products sorted by {}, direction: {}, page: {}, size: {}", sortBy, sortDirection, page, size);
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<Product> productPage = productRepository.findAll(pageable);

        log.info("Total products found: {}", productPage.getTotalElements());

        return productPage.map(productMapper::toDto);
    }
}
