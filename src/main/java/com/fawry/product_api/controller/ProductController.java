package com.fawry.product_api.controller;

import com.fawry.product_api.model.dto.ProductDto;
import com.fawry.product_api.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /******************************************************************************************************/
    /********************************* Product Management Endpoints ***************************************/
    /******************************************************************************************************/

    @PostMapping
    public ResponseEntity<ProductDto> addProduct(@Valid @RequestBody ProductDto productDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.addProduct(productDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable UUID id, @Valid @RequestBody ProductDto productDto) {
        return ResponseEntity.ok(productService.updateProduct(id, productDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProductDto> deleteProduct(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.deleteProduct(id));
    }

    /******************************************************************************************************/
    /******************************* Product Search and Filtering Endpoints *******************************/
    /******************************************************************************************************/

    @GetMapping("/category")
    public ResponseEntity<List<ProductDto>> getProductsByCategory(@RequestParam String categoryName) {
        return ResponseEntity.ok(productService.findByCategoryName(categoryName));
    }

    @GetMapping("/price")
    public ResponseEntity<List<ProductDto>> getProductsByPriceRange(@RequestParam double minPrice,
                                                                    @RequestParam double maxPrice) {
        return ResponseEntity.ok(productService.getProductsByPriceRange(minPrice, maxPrice));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductDto>> searchProducts(@RequestParam String keyword) {
        return ResponseEntity.ok(productService.searchProducts(keyword));
    }

    @GetMapping("/suggestions")
    public ResponseEntity<List<String>> getSearchSuggestions(@RequestParam String partial) {
        return ResponseEntity.ok(productService.getSearchSuggestions(partial));
    }

    @GetMapping("/page")
    public ResponseEntity<List<ProductDto>> getAllProductsWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(productService.getAllProductsWithPagination(page, size));
    }
}
