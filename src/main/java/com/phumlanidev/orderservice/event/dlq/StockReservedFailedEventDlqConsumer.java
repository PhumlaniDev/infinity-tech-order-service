package com.phumlanidev.orderservice.event.dlq;

import com.phumlanidev.commonevents.events.StockReservationFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockReservedFailedEventDlqConsumer {

  private final KafkaTemplate<String, StockReservationFailedEvent> kafkaTemplate;
  private static final String DLQ_TOPIC = "stock-reserved-failed-dlq";

  public void publishToDlq(String key, StockReservationFailedEvent event, Exception ex) {
    log.error("Publishing to DLQ: {}, error: {}", event, ex.getMessage());

    int attempts = 0;
    int maxAttempts = 5;
    long backOff = 1000;

    while (attempts < maxAttempts) {
      try {
        kafkaTemplate.send(DLQ_TOPIC, key, event);
        break;
      } catch (Exception e) {
        attempts++;
        log.warn("DLQ publish attempt {} failed: {}", attempts, e.getMessage());
        try {
          Thread.sleep(backOff);
          backOff *= 2;
        } catch (InterruptedException exception) {
          Thread.currentThread().interrupt();
        }
      }
    }
  }
}
