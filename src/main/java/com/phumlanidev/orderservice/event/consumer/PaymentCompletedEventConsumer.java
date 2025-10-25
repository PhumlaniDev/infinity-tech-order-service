package com.phumlanidev.orderservice.event.consumer;

import com.phumlanidev.commonevents.events.payment.PaymentCompletedEvent;
import com.phumlanidev.orderservice.enums.OrderStatus;
import com.phumlanidev.orderservice.model.Order;
import com.phumlanidev.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentCompletedEventConsumer {

  private final OrderRepository orderRepository;

  @KafkaListener(topics = "payment.completed", groupId = "order-group")
  public void handlePaymentCompleted(PaymentCompletedEvent event) {
    log.info("✅ Payment completed event received");
    // Implement the logic to handle payment completion, e.g., update order status, notify user, etc.
    Order order = orderRepository.findById(event.getOrderId())
            .orElseThrow(() -> new RuntimeException("Order not found for ID: " + event.getOrderId()));
    order.setOrderStatus(OrderStatus.PAID);
    orderRepository.save(order);
    log.info("✅ Order ID: {} marked as PAID", event.getOrderId());
  }
}
