package com.phumlanidev.orderservice.event.consumer;


import com.phumlanidev.commonevents.events.*;
import com.phumlanidev.orderservice.event.publiser.OrderFailedNotificationEventPublisher;
import com.phumlanidev.orderservice.event.publiser.PaymentRequestedEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class StockReservedEventConsumer {

//  private final OrderEventPublisher event;
  private final PaymentRequestedEventPublisher paymentRequestedEventPublisher;
  private final OrderFailedNotificationEventPublisher orderFailedNotificationEventPublisher;

  @Retryable(
          maxAttempts = 3,
          backoff = @Backoff(delay = 1000, multiplier = 2),
          retryFor = {RecoverableDataAccessException.class},
          noRetryFor = {IllegalAccessException.class}
  )
  @KafkaListener(
          topics = "stock.reserved",
          groupId = "order-group",
          containerFactory = "stockReservedEventConsumerContainerFactory",
          errorHandler = "stockReservedEventKafkaListenerErrorHandler"
  )
  public void onStockReserved(ConsumerRecord<String, StockReservedEvent> record) {
    StockReservedEvent event = record.value();
      log.info("✅ Stock reserved for order: {}. Proceeding to payment", event.getOrderId());

    paymentRequestedEventPublisher.publishPaymentRequestedEvent(PaymentRequestEvent.builder()
              .orderId(event.getOrderId())
              .userId(event.getUserId())
              .amount(calculateTotal(event.getItems()))
              .timestamp(Instant.now())
              .build());
  }

  @Retryable(
          maxAttempts = 3,
          backoff = @Backoff(delay = 1000, multiplier = 2),
          retryFor = {RecoverableDataAccessException.class},
          noRetryFor = {IllegalAccessException.class}
  )
  @KafkaListener(
          topics = "stock.failed",
          groupId = "order-group",
          containerFactory = "stockReservedFailedEventContainerFactory",
          errorHandler = "stockReservedFailedEventKafkaListenerErrorHandler"
  )
  public void onStockFailed(ConsumerRecord<String, StockReservationFailedEvent> record, Acknowledgment ack) {
    StockReservationFailedEvent event = record.value();
      log.warn("❌ Stock reservation failed for order {}: {}", event.getOrderId(), event.getReason());
      OrderFailedNotificationEvent notificationEvent = OrderFailedNotificationEvent.builder()
              .orderId(event.getOrderId())
              .userId(event.getUserId())
              .reason(event.getReason())
              .timestamp(Instant.now())
              .build();
    orderFailedNotificationEventPublisher.publishOrderFailedNotificationEvent(notificationEvent);
  }

  private BigDecimal calculateTotal(List<OrderPlacedEvent.OrderItemDto> item) {
    return BigDecimal.TEN.multiply(BigDecimal.valueOf(item.size()));
  }
}
