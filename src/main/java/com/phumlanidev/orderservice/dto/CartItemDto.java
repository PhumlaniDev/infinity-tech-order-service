package com.phumlanidev.orderservice.dto;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


/**
 * Comment: this is the placeholder for documentation.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDto {

  @NotNull(message = "Product ID is required")
  private Long productId; // foreign key reference
  @NotNull(message = "Quantity is required")
  private Integer quantity;
  @NotNull(message = "Price is required")
  private BigDecimal price;
}
