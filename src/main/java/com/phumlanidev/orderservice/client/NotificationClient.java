package com.phumlanidev.orderservice.client;

import com.phumlanidev.orderservice.config.JwtAuthenticationConverter;
import com.phumlanidev.orderservice.dto.OrderNotifyRequestDto;
import com.phumlanidev.orderservice.dto.PaymentConfirmationRequestDto;
import com.phumlanidev.orderservice.model.Order;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationClient {

  private final RestTemplate restTemplate;
  private final JwtAuthenticationConverter jwtAuthenticationConverter;
  private final HttpServletRequest request;

  public void orderNotifyPlaced(OrderNotifyRequestDto orderRequestDto, Jwt jwt) {
    log.info("Sending order notification for order ID: {}", orderRequestDto.getOrderId());

    String token = request.getHeader("Authorization");

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", token);

    HttpEntity<OrderNotifyRequestDto> entity = new HttpEntity<>(orderRequestDto, headers);

    String userEmail = jwtAuthenticationConverter.extractUserEmail(jwt);
    orderRequestDto.setToEmail(userEmail);

    //Build the notification request payload
    Map<String, Object> body = Map.of(
      "userId", orderRequestDto.getUserId(),
      "orderId", orderRequestDto.getOrderId(),
      "toEmail", userEmail,
      "total", orderRequestDto.getTotal(),
      "timestamp", orderRequestDto.getTimestamp()
    );

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

  public void sendPaymentConfirmation(Order order, Jwt jwt) {
    String userEmail = jwtAuthenticationConverter.extractUserEmail(jwt);
    PaymentConfirmationRequestDto request = PaymentConfirmationRequestDto.builder()
            .toEmail(userEmail)
            .orderId(order.getOrderId().toString())
            .totalAmount(order.getTotalPrice())
            .timestamp(Instant.now())
            .build();

    restTemplate.postForEntity(
            "http://notification-service/api/v1/notifications/payment-confirmation",
            request,
            Void.class
    );
  }
}
