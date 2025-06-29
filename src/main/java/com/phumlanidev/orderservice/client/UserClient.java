package com.phumlanidev.orderservice.client;

import com.phumlanidev.orderservice.config.KeycloakTokenProvider;
import com.phumlanidev.orderservice.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserClient {

  private final RestTemplate restTemplate;
  private final KeycloakTokenProvider tokenProvider;

  public List<UserDto> getAllUsers() {
    String url = "http://localhost:9100/api/v1/admin/users/";
    String token = tokenProvider.getAccessToken("order-service", "OPpIT3OXKkbDREqorem619wEnWwbsLsY");

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    HttpEntity<Void> request = new HttpEntity<>(headers);

    ResponseEntity<UserDto[]> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            request,
            UserDto[].class
    );

    return Arrays.asList(Objects.requireNonNull(response.getBody()));
//    Jwt token = jwtAuthenticationConverter.getCurrentJwt();
//
//    if (token == null || !token.hasClaim("sub")) {
//      throw new IllegalArgumentException("Invalid or missing JWT token");
//    }
//
//    String url = "http://localhost:9100/api/v1/admin/users/";
//
//    try {
//      String response = restTemplate.getForObject(url, String.class, token);
//      log.info("User details for {}: {}", userId, response);
//    } catch (Exception e) {
//      log.error("Error fetching user details for {}: {}", userId, e.getMessage());
//      throw e;
//    }
  }
}
