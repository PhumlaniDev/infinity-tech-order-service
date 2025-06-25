package com.phumlanidev.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentConfirmationRequestDto {

  private String toEmail;
  private String orderId;
  private BigDecimal totalAmount;
  private Instant timestamp;
}
