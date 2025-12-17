package com.phumlanidev.orderservice.client;

import com.phumlanidev.commonevents.events.product.ProductDto;
import com.phumlanidev.orderservice.config.AuthFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "product-service",
//        url = "${services.product-service.url}",
        path = "/api/v1/products",
        configuration = AuthFeignConfig.class
)
public interface ProductServiceClient {

  @GetMapping("/find/{productId}")
  ProductDto getProductById(@PathVariable("productId") Long productId);
}
