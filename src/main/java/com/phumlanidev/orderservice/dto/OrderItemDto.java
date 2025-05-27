package com.phumlanidev.orderservice.dto;

import java.math.BigDecimal;
import lombok.Data;

/**
 * Comment: this is the placeholder for documentation.
 */
@Data
public class OrderItemDto {

  private Long productId; // foreign key reference
  private Integer quantity;
  private BigDecimal price;
}
