package com.phumlanidev.orderservice.event;

import com.phumlanidev.orderservice.model.Order;
import com.phumlanidev.orderservice.model.OrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * Comment: this is the placeholder for documentation.
 */
@Service
@RequiredArgsConstructor
public class NotificationEvetPublisher {

  private final ApplicationEventPublisher publisher;

  /**
   * Comment: this is the placeholder for documentation.
   */
  public void publishOrderPlaced(String userId, Order order) {
    OrderPlacedEvent event = OrderPlacedEvent.builder()
            .userId(userId)
            .orderId(order.getOrderId())
            .total(order.getTotalPrice())
            .timestamp(order.getCreatedAt())
            .build();

    publisher.publishEvent(event);
  }
}
