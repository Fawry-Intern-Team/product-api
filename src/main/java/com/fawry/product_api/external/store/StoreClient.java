package com.fawry.product_api.external.store;

import com.fawry.product_api.external.stock.Stock;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;


@FeignClient(name = "store-service", url = "http://localhost:8087")
public interface StoreClient {

    @PostMapping("/api/stock/products")
    List<List<Stock>> getProductsWithStore(@RequestBody List<UUID> productIds);

    @GetMapping("/api/store/{id}")
    Store getStoreById(@PathVariable UUID id);
}

