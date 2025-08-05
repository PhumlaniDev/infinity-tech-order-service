package com.phumlanidev.orderservice.event.errorhandler;

import com.phumlanidev.commonevents.events.StockReservedEvent;
import com.phumlanidev.orderservice.event.dlq.StockReservedEventDlqConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@Component("stockReservedEventKafkaListenerErrorHandler")
@RequiredArgsConstructor
public class StockReservedEventKafkaConsumerErrorHandler implements KafkaListenerErrorHandler {

  private final StockReservedEventDlqConsumer dlqPublisher;

  @Override
  public Object handleError(Message<?> message, ListenerExecutionFailedException exception) {
    log.error("❗ Kafka error handler caught: {}", exception.getMessage());

    try {
      ConsumerRecord<String, StockReservedEvent> record = (ConsumerRecord<String, StockReservedEvent>) message.getPayload();
      log.warn("❌ DLQ fallback for message: {}", record.value());
      dlqPublisher.publishToDlq(record.key(), record.value(), exception);
    } catch (Exception e) {
      log.error("Failed to handle StockReservedEvent error: {}", e.getMessage(), e);
    }
    return null;
  }
}
