package com.phumlanidev.orderservice.client;

import com.phumlanidev.orderservice.config.AuthFeingConfig;
import com.phumlanidev.orderservice.dto.OrderNotifyRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(
        name = "notification-service",
        configuration = AuthFeingConfig.class
)
public interface NotificationClient {

  @PostMapping("/api/v1/notifications/email")
  void orderNotifyPlaced(OrderNotifyRequestDto orderRequestDto);
}
