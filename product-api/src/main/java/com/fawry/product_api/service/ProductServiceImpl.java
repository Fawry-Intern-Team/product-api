package com.fawry.product_api.service;

import com.fawry.product_api.mapper.ProductMapper;
import com.fawry.product_api.model.dto.ProductDto;
import com.fawry.product_api.model.entity.Product;
import com.fawry.product_api.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class  ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product addProduct(ProductDto productDto) {
        return productRepository.save(ProductMapper.INSTANCE.toEntity(productDto));
    }

    @Override
    @Cacheable(value = "product", key = "#id")
    public Product getProductById(Long id) {
        log.info("Fetching product from database for id: {}", id);
        // 🔴 Delay for testing (e.g. 3 seconds)
        try {
            Thread.sleep(3000); // 3000 ms = 3 seconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    @CacheEvict(value = "product", key = "#id")
    public Product updateProduct(Long id, ProductDto productDto) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        Product updatedProduct = ProductTransformation.transform(productDto);
        updatedProduct.setId(existingProduct.getId());

        return productRepository.save(updatedProduct);
    }

    @Override
    @CacheEvict(value = "product", key = "#id")
    public Product deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        productRepository.delete(product);
        return product;
    }
}
