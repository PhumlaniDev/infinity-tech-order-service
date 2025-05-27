package com.phumlanidev.orderservice.dto;

import com.phumlanidev.orderservice.model.OrderItem;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;

/**
 * Comment: this is the placeholder for documentation.
 */
@Data
@Builder
public class OrderDto {

  private Long orderId;
  private BigDecimal totalPrice;
  private List<OrderItem> items;
  private LocalDateTime placedAt;
  //  private PaymentStatus paymentStatus;
  //  private UUID orderNumber;
}
