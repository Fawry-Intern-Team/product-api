package com.fawry.product_api.mapper;

import com.fawry.product_api.model.dto.PaginatedResponseDto;
import com.fawry.product_api.model.dto.ProductDto;
import org.springframework.data.domain.Page;

public interface PaginatedResponseMapper {
    PaginatedResponseDto<ProductDto> toPaginatedResponse(Page<ProductDto> page);
}
