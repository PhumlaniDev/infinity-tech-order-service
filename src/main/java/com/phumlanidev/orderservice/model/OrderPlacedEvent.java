package com.phumlanidev.orderservice.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

/**
 * Comment: this is the placeholder for documentation.
 */
@Data
@Builder
public class OrderPlacedEvent {

  private String userId;
  private Long orderId;
  private BigDecimal total;
  private LocalDateTime timestamp;

}
