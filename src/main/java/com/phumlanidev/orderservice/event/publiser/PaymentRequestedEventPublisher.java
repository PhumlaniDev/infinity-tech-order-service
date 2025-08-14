package com.phumlanidev.orderservice.event.publiser;

import com.phumlanidev.commonevents.events.PaymentRequestEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PaymentRequestedEventPublisher extends BaseEventPublisher<PaymentRequestEvent> {
  // This class is responsible for publishing PaymentRequestEvent to Kafka
  private final KafkaTemplate<String, PaymentRequestEvent> kafkaTemplate;

  public PaymentRequestedEventPublisher(
          KafkaTemplate<String, PaymentRequestEvent> kafkaTemplate,
          KafkaTemplate<String, PaymentRequestEvent> kafkaTemplate1) {
    super(kafkaTemplate, "payment-created-dlq");
    this.kafkaTemplate = kafkaTemplate1;
  }

  public void publishPaymentRequestedEvent(PaymentRequestEvent event) {
    log.info("💳 Payment requested event received: {}", event);
    processWithDlq(event.getOrderId().toString(), event, () -> {
      kafkaTemplate.send("payment.created", event);
      log.info("📤 Published PaymentRequestEvent: {}", event);
    });
  }
}
