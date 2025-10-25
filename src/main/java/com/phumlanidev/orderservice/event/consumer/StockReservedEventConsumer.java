package com.phumlanidev.orderservice.event.consumer;


import com.phumlanidev.commonevents.events.StockReservationFailedEvent;
import com.phumlanidev.commonevents.events.StockReservedEvent;
import com.phumlanidev.commonevents.events.order.OrderFailedNotificationEvent;
import com.phumlanidev.commonevents.events.order.OrderPlacedEvent;
import com.phumlanidev.commonevents.events.payment.PaymentRequestEvent;
import com.phumlanidev.orderservice.event.publiser.OrderFailedNotificationEventPublisher;
import com.phumlanidev.orderservice.event.publiser.PaymentRequestedEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.kafka.annotation.KafkaListener;
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


      try {
        PaymentRequestEvent paymentRequestEvent = PaymentRequestEvent.builder()
                .orderId(event.getOrderId())
                .userId(event.getUserId())
                .amount(calculateTotal(event.getItems()))
                .timestamp(Instant.now())
                .build();

        paymentRequestedEventPublisher.publishPaymentRequestedEvent(paymentRequestEvent);
        log.info("📤 Published PaymentRequestEvent for order: {}", event.getOrderId());
      } catch (Exception ex) {
        log.error("Failed to publish PaymentRequestEvent for order {}: {}", event.getOrderId(), ex.getMessage(), ex);
        throw ex; // This will trigger the retry mechanism
      }
  }

  @Retryable(
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
  public void onStockFailed(ConsumerRecord<String, StockReservationFailedEvent> record) {
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
