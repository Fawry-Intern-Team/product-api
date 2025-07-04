package com.fawry.product_api.service;

import com.fawry.product_api.model.dto.ProductDto;
import com.fawry.product_api.model.entity.Product;

import java.util.List;

public interface ProductService {
    Product getProductById(Long id);

    List<Product> getAllProducts();

    Product addProduct(ProductDto productDto);

    Product updateProduct(Long id, ProductDto productDto);

    Product deleteProduct(Long id);
}
