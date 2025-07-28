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

}
