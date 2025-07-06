package com.fawry.product_api.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID", name = "category_id")
    private UUID id;  // Changed from String to UUID

    @Column(name = "category_name", nullable = false, length = 100, unique = true)
//    @FullTextField(analyzer = "standard")
//    @KeywordField(name = "name_sort")
    private String name;

//    @FullTextField(analyzer = "standard")
    private String description;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Product> products = new ArrayList<>();

}