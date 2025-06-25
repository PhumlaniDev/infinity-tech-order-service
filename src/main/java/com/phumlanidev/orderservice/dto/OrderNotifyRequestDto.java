package com.phumlanidev.orderservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class OrderNotifyRequestDto {

  @NotBlank(message = "User ID is required")
  private String userId;
  @NotNull(message = "Order ID is required")
  private Long orderId;
  @NotNull(message = "Total amount is required")
  private BigDecimal total;
  @NotNull(message = "Email recipient is required")
  private String toEmail;
  @NotNull(message = "Timestamp is required")
  private Instant timestamp;
}
