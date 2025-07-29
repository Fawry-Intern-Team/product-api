package com.fawry.product_api.external.store;

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
    private UUID productId;
    private String productName;
    private String productDescription;
    private String imageUrl;
    private Double price;
    private String categoryName;
    private UUID storeId;
    private String storeName;
    private String storeLocation;
}
