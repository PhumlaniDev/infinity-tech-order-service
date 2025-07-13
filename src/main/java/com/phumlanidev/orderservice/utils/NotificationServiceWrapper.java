package com.phumlanidev.orderservice.utils;

import com.phumlanidev.orderservice.client.NotificationClient;
import com.phumlanidev.orderservice.dto.OrderNotifyRequestDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceWrapper {

  private final NotificationClient notificationClient;

  @CircuitBreaker(name = "notificationService",
                  fallbackMethod = "sendNotificationFallback")
  @Retry(name = "notificationService")
  public void sendOrderPlacedNotification(OrderNotifyRequestDto orderRequestDto) {
    notificationClient.orderNotifyPlaced(orderRequestDto);
  }

  public void fallbackNotify(OrderNotifyRequestDto request, Throwable ex) {
    log.error("Notification failed: {}", ex.getMessage());
    // Optionally queue notification or save to DB for later retry
  }
}
