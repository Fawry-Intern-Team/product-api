package com.fawry.product_api.mapper;

import com.fawry.product_api.model.dto.ProductDto;
import com.fawry.product_api.model.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductDto toDto(Product product);

    Product toEntity(ProductDto productDto);

    List<ProductDto> toDtoList(List<Product> products);

    void updateProductFromDto(ProductDto dto, @MappingTarget Product entity);
}

