package com.fawry.product_api.service;

import com.fawry.product_api.mapper.CategoryMapper;
import com.fawry.product_api.mapper.ProductMapper;
import com.fawry.product_api.model.dto.CategoryDto;
import com.fawry.product_api.model.entity.Category;
import com.fawry.product_api.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public CategoryDto createCategory(CategoryDto dto) {
        Category saved = categoryRepository.save(categoryMapper.toEntity(dto));
        return categoryMapper.toDto(saved);
    }

    @Override
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toDto)
                .toList();
    }

}
