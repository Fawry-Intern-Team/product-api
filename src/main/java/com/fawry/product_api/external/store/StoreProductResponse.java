package com.fawry.product_api.external.store;

import com.fawry.product_api.model.dto.ProductDto;
import com.fawry.product_api.model.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreProductResponse {
    private ProductDto product;
    private UUID storeId;
    private String storeName;
    private String storeLocation;
}
