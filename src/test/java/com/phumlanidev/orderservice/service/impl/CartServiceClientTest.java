package com.phumlanidev.orderservice.service.impl;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phumlanidev.orderservice.dto.CartDto;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
class CartServiceClientTest {

  @Mock private RestTemplate restTemplate;
  @Mock private ObjectMapper objectMapper;

  CartServiceClient cartServiceClient;

  @BeforeEach
  void setUp() {
    cartServiceClient = new CartServiceClient(objectMapper, restTemplate);
  }

  @Test
  void getCart_success_callRestTemplateAndParseJson() throws Exception{
    String incomingToken = "Bearer token123";
    String expectedBearerToken = "token123";
    String rawJson = "{\"id\":\"cart-1\",\"items\":[]}";
    ResponseEntity<String> responseEntity = new ResponseEntity<>(rawJson, HttpStatus.OK);

    when(restTemplate.exchange(
            eq("http://localhost:9200/api/v1/cart"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class)))
            .thenReturn(responseEntity);

    CartDto expectedCart = new CartDto("user1", BigDecimal.ZERO, Collections.emptyList());
    when(objectMapper.readValue(eq(rawJson), eq(CartDto.class)))
            .thenReturn(expectedCart);

    CartDto result = cartServiceClient.getCart("user1", incomingToken);

    assertSame(expectedCart, result, "The returned CartDto should match the expected one");

    ArgumentCaptor<HttpEntity> captor = ArgumentCaptor.forClass(HttpEntity.class);
    verify(restTemplate).exchange( eq("http://localhost:9200/api/v1/cart"),
            eq(HttpMethod.GET),
            captor.capture(),
            eq(String.class));
    HttpHeaders sentHeaders = captor.getValue().getHeaders();
    assertEquals("Bearer " + expectedBearerToken, sentHeaders.getFirst(HttpHeaders.AUTHORIZATION));
    verify(objectMapper).readValue(rawJson, CartDto.class);
  }

  @Test
  void getCart_invalidToken_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> cartServiceClient.getCart("user1", null));
    assertThrows(IllegalArgumentException.class, () -> cartServiceClient.getCart("user1", "InvalidToken"));
  }

  @Test
  void getCart_parsingError_throwsRuntimeException() throws Exception{
    String rawJson = "{\"broken\": \"json\"}";
    ResponseEntity<String> responseEntity = new ResponseEntity<>(rawJson, HttpStatus.OK);
    when(restTemplate.exchange(
            eq("http://localhost:9200/api/v1/cart"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class)))
            .thenReturn(responseEntity);

    when(objectMapper.readValue(eq(rawJson), eq(CartDto.class)))
            .thenThrow(new JsonMappingException(null, "invalid"));

    RuntimeException ex = assertThrows(RuntimeException.class,
            () -> cartServiceClient.getCart("user-1", "Bearer token123"));
    assertTrue(ex.getMessage().contains("Failed to parse cart JSON"));
  }

  @Test
  void getCart_restClientException_propagates() {
    when(restTemplate.exchange(
            eq("http://localhost:9200/api/v1/cart"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class)))
            .thenThrow(new RestClientException("Service down"));

    RuntimeException ex = assertThrows(RuntimeException.class,
            () -> cartServiceClient.getCart("user-1", "Bearer token123"));
    assertEquals("Service down", ex.getMessage());
  }
}