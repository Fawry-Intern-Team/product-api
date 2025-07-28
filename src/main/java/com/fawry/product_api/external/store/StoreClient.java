package com.fawry.product_api.external.store;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;


@FeignClient(name = "store-api", url = "http://localhost:8087")
public interface StoreClient {

    @PostMapping("/api/store/products")
    List<StoreProductResponse> getProductsWithStore(@RequestBody List<UUID> productIds);
}

