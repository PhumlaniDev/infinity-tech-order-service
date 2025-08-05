package com.phumlanidev.orderservice.event.publiser;

import com.phumlanidev.commonevents.events.OrderPlacedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderPlacedEventPublisher extends BaseEventPublisher<OrderPlacedEvent> {

  private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

  protected OrderPlacedEventPublisher(
          KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate,
          KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate1) {
    super(kafkaTemplate, "order-placed-dlq");
    this.kafkaTemplate = kafkaTemplate1;
  }

  public void publishOrderPlacedEvent(OrderPlacedEvent event) {
    log.info("📦 Order placed event received: {}", event);
    processWithDlq(event.getOrderId().toString(), event, () -> {
      kafkaTemplate.send("order.placed", event);
      log.info("📤 Published OrderPlacedEvent: {}", event);
    });
  }
}