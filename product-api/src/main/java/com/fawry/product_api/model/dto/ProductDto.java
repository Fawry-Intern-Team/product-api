package com.fawry.product_api.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotBlank(message = "Price is required")
    private double price;
}
