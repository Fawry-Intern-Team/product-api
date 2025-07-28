package com.fawry.product_api.service;

import com.fawry.product_api.mapper.CategoryMapper;
import com.fawry.product_api.mapper.ProductMapper;
import com.fawry.product_api.model.dto.ProductDto;
import com.fawry.product_api.model.entity.Category;
import com.fawry.product_api.model.entity.Product;
import com.fawry.product_api.repository.CategoryRepository;
import com.fawry.product_api.repository.ProductRepository;
import com.fawry.product_api.util.ProductSpecifications;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.springframework.data.jpa.repository.query.KeysetScrollSpecification.createSort;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository,
            ProductMapper productMapper,
            CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
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
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        log.info("Product found: {}", existingProduct.getName());

        return productMapper.toDto(existingProduct);
    }

    @Override
    public ProductDto updateProduct(UUID id, ProductDto productDto) {
        log.info("Fetching product for update with id: {}", id);
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
        log.info("Fetching product for deletion with id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        log.info("Deleting product with id: {}", id);
        productRepository.delete(product);
        log.info("Product deleted successfully: {}", product.getName());

        return productMapper.toDto(product);

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
    public List<ProductDto> getProductsByIds(List<UUID> productIds) {
        log.info("Fetching products by IDs: {}", productIds);
        List<Product> products = productRepository.findAllById(productIds);
        if (products.isEmpty()) {
            log.warn("No products found for the provided IDs: {}", productIds);
        } else {
            log.info("Found {} products for the provided IDs", products.size());
        }
        return productMapper.toDtoList(products);
    }

    public Page<ProductDto> getFilteredProducts(String keyword, String category, double min, double max,
                                                String sortBy, String sortDirection, int page, int size) {

        // Create specification with all filters
        Specification<Product> specification = ProductSpecifications.withAllFilters(keyword, category, min, max);

        // Create sort object
        Sort sort = createSortObject(sortBy, sortDirection);

        // Create pageable object
        Pageable pageable = PageRequest.of(page, size, sort);

        // Execute query with specifications
        Page<Product> productPage = productRepository.findAll(specification, pageable);

        // Convert to DTO
        return productPage.map(productMapper::toDto);
    }

    private Sort createSortObject(String sortBy, String sortDirection) {
        // Default values
        if (sortBy == null || sortBy.trim().isEmpty()) {
            sortBy = "id";
        }

        Sort.Direction direction = Sort.Direction.ASC;
        if ("desc".equalsIgnoreCase(sortDirection)) {
            direction = Sort.Direction.DESC;
        }

        // Map sort fields
        String actualSortField;
        switch (sortBy.toLowerCase()) {
            case "price":
                actualSortField = "price";
                break;
            case "name":
                actualSortField = "name";
                break;
            case "date":
            case "created":
                actualSortField = "createdDate";
                break;
            case "popularity":
                actualSortField = "viewCount";
                break;
            case "category":
                actualSortField = "category.name";
                break;
            default:
                actualSortField = "id";
                break;
        }

        return Sort.by(direction, actualSortField);
    }

}
