package com.fawry.product_api.mapper;

import com.fawry.product_api.model.dto.PaginatedResponseDto;
import com.fawry.product_api.model.dto.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class PaginatedResponseMapperImpl implements  PaginatedResponseMapper {

    @Override
    public PaginatedResponseDto<ProductDto> toPaginatedResponse(Page<ProductDto> page) {
        PaginatedResponseDto<ProductDto> response = new PaginatedResponseDto<>();
        response.setContent(page.getContent());
        response.setPageNumber(page.getNumber());
        response.setPageSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setHasNext(page.hasNext());
        response.setHasPrevious(page.hasPrevious());
        return response;
    }
}
