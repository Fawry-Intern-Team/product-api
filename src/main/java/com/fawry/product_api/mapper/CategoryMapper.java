package com.fawry.product_api.mapper;

import com.fawry.product_api.model.dto.CategoryDto;
import com.fawry.product_api.model.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDto toDto(Category category);

    Category toEntity(CategoryDto dto);
}