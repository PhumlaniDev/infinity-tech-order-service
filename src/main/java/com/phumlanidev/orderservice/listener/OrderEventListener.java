package com.phumlanidev.orderservice.listener;

import com.phumlanidev.orderservice.model.OrderPlacedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Comment: this is the placeholder for documentation.
 */
@Component
@Slf4j
public class OrderEventListener {

  /**
   * Comment: this is the placeholder for documentation.
   */
  @EventListener
  public void handleOrderPlacedEvent(OrderPlacedEvent event) {
    log.info("📢 Order Placed Event Received:");
    log.info("🧑 User ID: {}", event.getUserId());
    log.info("📦 Order ID: {}", event.getOrderId());
    log.info("💰 Total: {}", event.getTotal());
    log.info("🕒 Timestamp: {}", event.getTimestamp());
  }
}
