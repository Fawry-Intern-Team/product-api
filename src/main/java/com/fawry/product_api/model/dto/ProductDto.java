package com.fawry.product_api.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDto {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
    private String name;

    private String description;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be a positive number or zero")
    private double price;

    @NotBlank(message = "Image URL is required")
    private String imageUrl;

    @Min(value = 0, message = "Stock quantity must be a positive number or zero")
    private Integer stockQuantity = 0;

    @NotBlank(message = "Category is required")
    private String categoryName;
}
