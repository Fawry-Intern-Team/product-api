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
    public List<ProductDto> getAllProducts() {
        log.info("Fetching all products from database");
        List<Product> products = productRepository.findAll();
        log.info("Total products found: {}", products.size());

        return productMapper.toDtoList(products);
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
    public List<ProductDto> findByCategoryName(String categoryName) {
        log.info("Fetching products by category name: {}", categoryName);
        List<Product> productsList = productRepository.findByCategoryName(categoryName);
        return productMapper.toDtoList(productsList);
    }

    @Override
    public List<ProductDto> getProductsByPriceRange(double minPrice, double maxPrice) {
        log.info("Fetching products by price range: {} - {}", minPrice, maxPrice);
        List<Product> productsList = productRepository.findByPriceBetween(minPrice, maxPrice);
        return productMapper.toDtoList(productsList);
    }

    @Override
    public List<ProductDto> searchProducts(String keyword) {
        log.info("Searching products with keyword: {}", keyword);
        List<Product> productsList = productRepository.searchProducts(keyword);
        if (productsList.isEmpty()) {
            log.warn("No products found for keyword: {}", keyword);
        } else {
            log.info("Found {} products for keyword: {}", productsList.size(), keyword);
        }
        return productMapper.toDtoList(productsList);
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
    public List<ProductDto> getAllProductsWithPagination(int page, int size) {
        log.info("Fetching all products with pagination, page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.findAll(pageable);

        log.info("Total products found: {}", productPage.getTotalElements());

        return productMapper.toDtoList(productPage.getContent());
    }

    @Override
    public List<ProductDto> getAllProductsSorted(String sortBy, String sortDirection, int page, int size) {
        log.info("Fetching all products sorted by {}, direction: {}, page: {}, size: {}", sortBy, sortDirection, page, size);
        Pageable pageable = PageRequest.of(page, size, "asc".equalsIgnoreCase(sortDirection) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending());
        Page<Product> productPage = productRepository.findAll(pageable);

        log.info("Total products found: {}", productPage.getTotalElements());

        return productMapper.toDtoList(productPage.getContent());

    }
}
