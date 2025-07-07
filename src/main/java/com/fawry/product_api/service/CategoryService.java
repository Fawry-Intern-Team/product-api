package com.fawry.product_api.service;

import com.fawry.product_api.model.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(CategoryDto dto);
    List<CategoryDto> getAllCategories();
}
