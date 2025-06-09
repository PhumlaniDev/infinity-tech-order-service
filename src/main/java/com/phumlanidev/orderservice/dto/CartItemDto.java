package com.phumlanidev.orderservice.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;


/**
 * Comment: this is the placeholder for documentation.
 */
@Data
public class CartItemDto {

  @NotNull(message = "Product ID is required")
  private Long productId; // foreign key reference
  @NotNull(message = "Quantity is required")
  private Integer quantity;
  @NotNull(message = "Price is required")
  private BigDecimal price;
}
