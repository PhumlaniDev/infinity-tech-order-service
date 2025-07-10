package com.phumlanidev.orderservice.client;

import com.phumlanidev.orderservice.dto.OrderNotifyRequestDto;
import com.phumlanidev.orderservice.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
class NotificationClientxTest {

  @InjectMocks private NotificationClientx notificationClientx;
  @Mock private RestTemplate restTemplate;
  @Mock private SecurityUtils securityUtils;
  @Mock private HttpServletRequest request;

  @Test
  void orderNotifyPlacedSuccessfully() {
    String userId = "456";
    String userEmail = "example@email.com";
    String token = "Bearer token";
    Long orderId = 123L;

    when(securityUtils.getCurrentEmail()).thenReturn(userEmail);

    OrderNotifyRequestDto orderRequestDto = OrderNotifyRequestDto.builder()
            .userId(userId)
            .orderId(orderId)
            .total(BigDecimal.valueOf(100))
            .toEmail(userEmail)
            .timestamp(Instant.now())
            .build();

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", token);
    HttpEntity<OrderNotifyRequestDto> entity = new HttpEntity<>(orderRequestDto, headers);

    when(request.getHeader("Authorization")).thenReturn("Bearer token");
    when(securityUtils.getCurrentEmail()).thenReturn("user@example.com");

    notificationClientx.orderNotifyPlaced(orderRequestDto);

    verify(restTemplate).postForEntity(
            "http://localhost:9500/api/v1/notifications/email",
            entity,
            Void.class
    );
  }

  @Test
  void orderNotifyPlacedFailsWithInvalidToken() {

    String userId = "456";
    String userEmail = "example@email.com";
    Long orderId = 123L;

    when(securityUtils.getCurrentEmail()).thenReturn(userEmail);

    OrderNotifyRequestDto orderRequestDto = OrderNotifyRequestDto.builder()
            .userId(userId)
            .orderId(orderId)
            .total(BigDecimal.valueOf(100))
            .toEmail(userEmail)
            .timestamp(Instant.now())
            .build();

    when(request.getHeader("Authorization")).thenReturn("Bearer token");
    when(securityUtils.getCurrentEmail()).thenReturn("user@example.com");

    notificationClientx.orderNotifyPlaced(orderRequestDto);

    verify(restTemplate).postForEntity(
            anyString(),
            any(HttpEntity.class),
            eq(Void.class)
    );
  }

  @Test
  void orderNotifyPlacedFailsWithRestTemplateException() {
    String userId = "456";
    String userEmail = "example@email.com";
    String token = "Bearer token";
    Long orderId = 123L;

    when(securityUtils.getCurrentEmail()).thenReturn(userEmail);

    OrderNotifyRequestDto orderRequestDto = OrderNotifyRequestDto.builder()
            .userId(userId)
            .orderId(orderId)
            .total(BigDecimal.valueOf(100))
            .toEmail(userEmail)
            .timestamp(Instant.now())
            .build();

    when(request.getHeader("Authorization")).thenReturn(token);
    when(securityUtils.getCurrentEmail()).thenReturn(userEmail);
    doThrow(new RuntimeException("RestTemplate error")).when(restTemplate).postForEntity(
            eq("http://localhost:9500/api/v1/notifications/email"),
            any(HttpEntity.class),
            eq(Void.class)
    );

    notificationClientx.orderNotifyPlaced(orderRequestDto);

    verify(restTemplate).postForEntity(
            eq("http://localhost:9500/api/v1/notifications/email"),
            any(HttpEntity.class),
            eq(Void.class)
    );
  }
}