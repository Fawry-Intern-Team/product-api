package com.fawry.product_api.controller;

import com.fawry.product_api.mapper.PaginatedResponseMapper;
import com.fawry.product_api.mapper.PaginatedResponseMapperImpl;
import com.fawry.product_api.model.dto.PaginatedResponseDto;
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
    private final PaginatedResponseMapper paginatedResponseMapper;

    @Autowired
    public ProductController(ProductService productService,
                             PaginatedResponseMapper paginatedResponseMapper){
        this.productService = productService;
        this.paginatedResponseMapper = paginatedResponseMapper;
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

    @GetMapping
    public ResponseEntity<Page<ProductDto>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(productService.getAllProductsWithPagination(page, size));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable UUID id, @RequestBody ProductDto productDto) {
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
    public ResponseEntity<PaginatedResponseDto<ProductDto>> getProductsByCategory(
            @RequestParam String categoryName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<ProductDto> productPage = productService.findByCategoryName(categoryName, page, size);
        PaginatedResponseDto<ProductDto> response = paginatedResponseMapper.toPaginatedResponse(productPage);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/price")
    public ResponseEntity<PaginatedResponseDto<ProductDto>> getProductsByPriceRange(
            @RequestParam double minPrice,
            @RequestParam double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<ProductDto> productPage = productService.getProductsByPriceRange(minPrice, maxPrice, page, size);
        PaginatedResponseDto<ProductDto> response = paginatedResponseMapper.toPaginatedResponse(productPage);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<PaginatedResponseDto<ProductDto>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<ProductDto> productPage = productService.searchProducts(keyword, page, size);
        PaginatedResponseDto<ProductDto> response = paginatedResponseMapper.toPaginatedResponse(productPage);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/suggestions")
    public ResponseEntity<List<String>> getSearchSuggestions(@RequestParam String partial) {
        return ResponseEntity.ok(productService.getSearchSuggestions(partial));
    }

    @GetMapping("/sort")
    public ResponseEntity<PaginatedResponseDto<ProductDto>> getAllProductsSorted(
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<ProductDto> productPage = productService.getAllProductsSorted(sortBy, sortDirection, page, size);
        PaginatedResponseDto<ProductDto> response = paginatedResponseMapper.toPaginatedResponse(productPage);
        return ResponseEntity.ok(response);
    }

}
