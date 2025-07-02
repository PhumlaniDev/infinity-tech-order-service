package com.phumlanidev.orderservice.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserClient {

//  private final RestTemplate restTemplate;
//  private final KeycloakTokenProvider tokenProvider;
//
//  public List<UserDto> getAllUsers() {
//    String url = "http://localhost:9100/api/v1/admin/users/";
//    String token = tokenProvider.getAccessToken("order-service", "");
//
//    HttpHeaders headers = new HttpHeaders();
//    headers.setBearerAuth(token);
//    HttpEntity<Void> request = new HttpEntity<>(headers);
//
//    ResponseEntity<UserDto[]> response = restTemplate.exchange(
//            url,
//            HttpMethod.GET,
//            request,
//            UserDto[].class
//    );
//
//    return Arrays.asList(Objects.requireNonNull(response.getBody()));
//  }
}
