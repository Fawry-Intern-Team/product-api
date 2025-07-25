package com.fawry.product_api.repository;

import com.fawry.product_api.model.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    @Query("SELECT p FROM Product p WHERE p.category.name = :categoryName")
    Page<Product> findByCategoryName(@Param("categoryName") String categoryName, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    Page<Product> findByPriceBetween(@Param("minPrice") double minPrice, @Param("maxPrice") double maxPrice,
                                     Pageable pageable);

    @Query(value = "SELECT * FROM products WHERE " +
            "to_tsvector('english', product_name || ' ' || description) @@ plainto_tsquery('english', :keyword)",
            nativeQuery = true)
    Page<Product> searchProducts(@Param("keyword") String keyword,
                                 Pageable pageable);

    @Query(value = "SELECT DISTINCT product_name FROM products WHERE " +
            "to_tsvector('english', product_name) @@ plainto_tsquery('english', :partial)",
            nativeQuery = true)
    List<String> getSearchSuggestions(@Param("partial") String partial);

}
