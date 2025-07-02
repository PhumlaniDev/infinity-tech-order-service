package com.phumlanidev.orderservice.client;

import com.phumlanidev.orderservice.dto.OrderNotifyRequestDto;
import com.phumlanidev.orderservice.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationClient {

  private final RestTemplate restTemplate;
  private final SecurityUtils securityUtils;
  private final HttpServletRequest request;

  public void orderNotifyPlaced(OrderNotifyRequestDto orderRequestDto) {
    log.info("Sending order notification for order ID: {}", orderRequestDto.getOrderId());

    String token = request.getHeader("Authorization");

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", token);

    HttpEntity<OrderNotifyRequestDto> entity = new HttpEntity<>(orderRequestDto, headers);

    String userEmail = securityUtils.getCurrentEmail();
    orderRequestDto.setToEmail(userEmail);

    try {
      restTemplate.postForEntity(  "http://localhost:9500/api/v1/notifications/email",
              entity,
              Void.class);
      log.info("Order notification sent successfully for order ID: {}", orderRequestDto.getOrderId());
    } catch (Exception e) {
      log.error("Failed to send order notification for order ID: {}. Error: {}",
                orderRequestDto.getOrderId(), e.getMessage());
    }
  }
}
