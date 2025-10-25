package com.phumlanidev.orderservice.event.errorhandler;

import com.phumlanidev.commonevents.events.order.OrderFailedNotificationEvent;
import com.phumlanidev.orderservice.event.dlq.OrderFailedNotificationEventDlqPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component("orderFailedNotificationKafkaListenerErrorHandler")
@RequiredArgsConstructor
@Slf4j
public class OrderFailedNotificationKafkaListenerErrorHandler implements KafkaListenerErrorHandler {

  private final OrderFailedNotificationEventDlqPublisher dlqPublisher;

  @Override
  public Object handleError(Message<?> message, ListenerExecutionFailedException exception) {
    log.error("❗ Kafka error handler caught: {}", exception.getMessage());

    try {
      ConsumerRecord<String, OrderFailedNotificationEvent> record =
              (ConsumerRecord<String, OrderFailedNotificationEvent>) message.getPayload();
       log.warn("❌ DLQ fallback for message: {}", record.value());
      dlqPublisher.publishToDlq(record.key(), record.value(), exception);
    } catch (Exception e) {
      log.error("Failed to handle OrderFailedNotificationEvent error: {}", e.getMessage(), e);
    }
    return null;
  }
}
