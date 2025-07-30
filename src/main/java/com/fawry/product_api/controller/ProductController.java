package com.fawry.product_api.controller;

import com.fawry.product_api.external.store.StoreProductResponse;
import com.fawry.product_api.model.dto.ProductDto;
import com.fawry.product_api.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
    public ProductController(ProductService productService){
        this.productService = productService;
    }

    /******************************************************************************************************/
    /********************************* Product Management Endpoints ***************************************/
    /******************************************************************************************************/

    @PostMapping
    public ResponseEntity<ProductDto> addProduct(@RequestBody ProductDto productDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.addProduct(productDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PutMapping
    public ResponseEntity<ProductDto> updateProduct(@RequestParam UUID id, @RequestBody ProductDto productDto) {
        return ResponseEntity.ok(productService.updateProduct(id, productDto));
    }

    @DeleteMapping
    public ResponseEntity<ProductDto> deleteProduct(@RequestParam UUID id) {
        return ResponseEntity.ok(productService.deleteProduct(id));
    }

    /******************************************************************************************************/
    /******************************* Product Search and Filtering Endpoints *******************************/
    /******************************************************************************************************/

    @GetMapping("/suggestions")
    public ResponseEntity<List<String>> getSearchSuggestions(@RequestParam String partial) {
        return ResponseEntity.ok(productService.getSearchSuggestions(partial));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductDto>> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") double min,
            @RequestParam(defaultValue = "0") double max,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        Page<ProductDto> products = productService.getFilteredProducts(
                keyword, category, min, max, sortBy, sortDirection, page, size);

        return ResponseEntity.ok(products);
    }


    /******************************************************************************************************/
    /********************************* Product Store Integration Endpoints ********************************/
    /******************************************************************************************************/

    @GetMapping
    public ResponseEntity<List<StoreProductResponse>> getProductsWithStoreByIds(
            @RequestParam List<UUID> productIds) {
        return ResponseEntity.ok(productService.fetchProductDetailsWithStore(productIds));
    }

}
