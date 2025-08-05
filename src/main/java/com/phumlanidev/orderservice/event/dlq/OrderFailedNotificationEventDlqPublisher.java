package com.phumlanidev.orderservice.event.dlq;

import com.phumlanidev.commonevents.events.OrderFailedNotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderFailedNotificationEventDlqPublisher {

   private final KafkaTemplate<String, OrderFailedNotificationEvent> kafkaTemplate;
   private static final String DLQ_TOPIC = "order-failed-notification-dlq";

  public void publishToDlq(String key, OrderFailedNotificationEvent event, Exception ex) {
    log.error("Publishing to DLQ: {}, error: {}", event, ex.getMessage());

    int attempts = 0;
    int maxAttempts = 5;
    long backOff = 1000;
    while (attempts < maxAttempts) {
      try {
        kafkaTemplate.send(DLQ_TOPIC, key, event);
        log.info("Successfully published to DLQ: {}", event);
        break;
      } catch (Exception e) {
        attempts++;
        log.warn("DLQ publish attempt {} failed: {}", attempts, e.getMessage());
        try {
          Thread.sleep(backOff);
          backOff *= 2; // Exponential backoff
        } catch (InterruptedException exception) {
          Thread.currentThread().interrupt();
        }
      }
    }
  }
}
