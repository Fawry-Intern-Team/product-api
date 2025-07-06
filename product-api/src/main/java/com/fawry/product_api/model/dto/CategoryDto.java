package com.fawry.product_api.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryDto {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100,message = "Product name must be between 2 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z0-9 ]+$", message = "Product name can only contain letters, numbers, and spaces")
    private String name;

    private String description;

}