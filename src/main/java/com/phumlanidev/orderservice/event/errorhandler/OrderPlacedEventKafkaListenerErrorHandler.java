package com.phumlanidev.orderservice.event.errorhandler;

import com.phumlanidev.commonevents.events.OrderPlacedEvent;
import com.phumlanidev.orderservice.event.dlq.OrderPlacedEventDlqPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component("stockReservedEventKafkaConsumerErrorHandler")
@RequiredArgsConstructor
@Slf4j
public class OrderPlacedEventKafkaListenerErrorHandler implements KafkaListenerErrorHandler {

  private final OrderPlacedEventDlqPublisher dlqPublisher;

  @Override
  public Object handleError(Message<?> message, ListenerExecutionFailedException exception) {
    log.error("❗ Kafka error handler caught: {}", exception.getMessage());

    try {
      ConsumerRecord<String, OrderPlacedEvent> record =
              (ConsumerRecord<String, OrderPlacedEvent>) message.getPayload();
      log.warn("❌ DLQ fallback for message: {}", record.value());
      dlqPublisher.publishToDlq(record.key(), record.value(), exception);
    } catch (Exception e) {
      log.error("Failed to handle OrderPlacedEvent error: {}", e.getMessage(), e);
    }
    return null;
  }
}
