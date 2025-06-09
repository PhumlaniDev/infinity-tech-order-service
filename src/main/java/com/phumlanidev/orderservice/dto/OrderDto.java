package com.phumlanidev.orderservice.dto;

import com.phumlanidev.orderservice.model.OrderItem;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Comment: this is the placeholder for documentation.
 */
@Data
@Builder
public class OrderDto {

  @NotNull(message = "Order ID is required")
  private Long orderId;
  @NotNull(message = "Total price is required")
  private BigDecimal totalPrice;
  @NotEmpty(message = "Order items cannot be empty")
  private List<OrderItem> items;
  @NotNull(message = "User ID is required")
  private LocalDateTime placedAt;
  //  private PaymentStatus paymentStatus;
  //  private UUID orderNumber;
}
