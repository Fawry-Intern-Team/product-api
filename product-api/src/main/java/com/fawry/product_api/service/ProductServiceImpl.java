package com.fawry.product_api.service;

import com.fawry.product_api.mapper.ProductMapper;
import com.fawry.product_api.model.dto.ProductDto;
import com.fawry.product_api.model.entity.Product;
import com.fawry.product_api.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    public ProductDto addProduct(ProductDto productDto) {
        log.info("Adding product to database: {}", productDto);
        productRepository.save(productMapper.toEntity(productDto));

        log.info("Product added successfully: {}", productDto.getName());
        return productDto;
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
}
