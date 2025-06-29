package com.phumlanidev.orderservice.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Comment: this is the placeholder for documentation.
 */
@Data
@Builder
public class OrderPlacedEvent {

  private String userId;
  private Long orderId;
  private BigDecimal total;
  private Instant timestamp;

}
