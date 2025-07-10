package com.phumlanidev.orderservice.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

public class AuthFeingConfig {

  @Bean
  public RequestInterceptor requestInterceptor() {
    return requestTemplate -> {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication != null && authentication.getCredentials() instanceof Jwt jwt) {
        requestTemplate.header("Authorization", "Bearer " + jwt.getTokenValue());
      }
    };
  }
}
