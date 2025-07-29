package com.fawry.product_api.repository;

import com.fawry.product_api.model.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> , JpaSpecificationExecutor<Product>{

    @Query(value = "SELECT DISTINCT product_name FROM products WHERE " +
            "to_tsvector('english', product_name) @@ plainto_tsquery('english', :partial)",
            nativeQuery = true)
    List<String> getSearchSuggestions(@Param("partial") String partial);


    @Query(value = """
        SELECT p.* FROM products p 
        LEFT JOIN categories c ON p.category_id = c.category_id
        WHERE p.is_deleted = false
        AND (:tsQuery IS NULL OR :tsQuery = '' OR 
             to_tsvector('english', COALESCE(p.product_name, '') || ' ' || 
                        COALESCE(p.description, '')) @@ plainto_tsquery('english', :tsQuery))
        AND (:category IS NULL OR :category = '' OR c.category_name = :category)
        AND (:min IS NULL OR p.product_price >= :min)
        AND (:max IS NULL OR p.product_price <= :max)
        ORDER BY 
            CASE WHEN :sortBy = 'name' AND :sortDir = 'asc' THEN p.product_name END ASC,
            CASE WHEN :sortBy = 'name' AND :sortDir = 'desc' THEN p.product_name END DESC,
            CASE WHEN :sortBy = 'price' AND :sortDir = 'asc' THEN p.product_price END ASC,
            CASE WHEN :sortBy = 'price' AND :sortDir = 'desc' THEN p.product_price END DESC,
            CASE WHEN :sortBy = 'relevance' AND :tsQuery IS NOT NULL AND :tsQuery != '' THEN 
                ts_rank(to_tsvector('english', COALESCE(p.product_name, '') || ' ' || 
                                   COALESCE(p.description, '')), 
                       plainto_tsquery('english', :tsQuery)) END DESC,
            p.product_id DESC
        """,
            countQuery = """
        SELECT COUNT(*) FROM products p 
        LEFT JOIN categories c ON p.category_id = c.category_id
        WHERE p.is_deleted = false
        AND (:tsQuery IS NULL OR :tsQuery = '' OR 
             to_tsvector('english', COALESCE(p.product_name, '') || ' ' || 
                        COALESCE(p.description, '')) @@ plainto_tsquery('english', :tsQuery))
        AND (:category IS NULL OR :category = '' OR c.category_name = :category)
        AND (:min IS NULL OR p.product_price >= :min)
        AND (:max IS NULL OR p.product_price <= :max)
        """,
            nativeQuery = true)
    Page<Product> searchFullText(
            @Param("tsQuery") String tsQuery,
            @Param("category") String category,
            @Param("min") Double min,
            @Param("max") Double max,
            @Param("sortBy") String sortBy,
            @Param("sortDir") String sortDir,
            Pageable pageable
    );

}
