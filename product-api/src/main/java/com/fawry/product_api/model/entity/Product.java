package com.fawry.product_api.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class Product {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "product_id" , nullable = false)
    private UUID id;

    @Column(name = "product_name", nullable = false, length = 100)
//    @FullTextField(analyzer = "standard") // Full-text searchable
//    @KeywordField(name = "name_sort") // For sorting
    private String name;

    @Column(columnDefinition = "TEXT")
//    @FullTextField(analyzer = "standard") // Full-text searchable
    private String description;

    @Column(name = "product_price", nullable = false)
//    @GenericField // For filtering/sorting
    private Double price;

    @Column(name = "img_url", columnDefinition = "TEXT")
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
//    @IndexedEmbedded
    private Category category;

    @Column(name = "stock_quantity")
//    @GenericField
    private Integer stockQuantity = 0;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;
}
