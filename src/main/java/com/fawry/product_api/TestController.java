package com.fawry.product_api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/products")
public class TestController {
    @RequestMapping("/test")
    public String test() {
        return "Product API is running!";
    }
}
