package com.phumlanidev.orderservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phumlanidev.orderservice.dto.CartDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


/**
 * Comment: this is the placeholder for documentation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceClient {

  @Autowired
  private ObjectMapper objectMapper;
  private final RestTemplate restTemplate;

  /**
   * Comment: this is the placeholder for documentation.
   */
  public CartDto getCart(String userId, String token) {
    if (token != null && token.startsWith("Bearer ")) {
      token = token.substring(7); // Remove "Bearer " prefix
    } else {
      log.error("Invalid token format for user {}", userId);
      throw new IllegalArgumentException("Invalid token format");
    }

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);

    HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
    ResponseEntity<String> rawResponse = restTemplate.exchange(
            "http://localhost:8085/api/v1/cart",
            HttpMethod.GET,
            requestEntity,
            String.class
    );

    String jsonBody = String.valueOf(rawResponse.getBody());
    log.debug("🛒 Raw cart JSON response: {}", jsonBody);

    try {
      return objectMapper.readValue(jsonBody, CartDto.class);
    } catch (JsonProcessingException e) {
      log.error("❌ Failed to deserialize cart", e);
      throw new RuntimeException("Failed to parse cart JSON", e);
    }
  }
}
