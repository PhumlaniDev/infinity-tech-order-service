package com.phumlanidev.orderservice.client;

import com.phumlanidev.orderservice.config.AuthFeignConfig;
import com.phumlanidev.orderservice.dto.OrderNotifyRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(
        name = "notification-service",
        configuration = AuthFeignConfig.class
)
public interface NotificationClient {

  @PostMapping("/api/v1/notifications/email")
  void orderNotifyPlaced(OrderNotifyRequestDto orderRequestDto);
}
