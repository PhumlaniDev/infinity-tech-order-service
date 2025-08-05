package com.phumlanidev.orderservice.event.publiser;

import com.phumlanidev.commonevents.events.OrderFailedNotificationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderFailedNotificationEventPublisher extends BaseEventPublisher<OrderFailedNotificationEvent> {

    private final StreamBridge streamBridge;

    protected OrderFailedNotificationEventPublisher(
            KafkaTemplate<String, OrderFailedNotificationEvent> kafkaTemplate, StreamBridge streamBridge) {
        super(kafkaTemplate, "order-failed-notification-dlq");
        this.streamBridge = streamBridge;
    }

    public void publishOrderFailedNotificationEvent(OrderFailedNotificationEvent event) {
        log.info("❌ Order failed notification event received: {}", event);
        processWithDlq(event.getOrderId().toString(), event, () -> {
            streamBridge.send("orderFailedNotificationEvent-out-0", event);
            log.info("📤 Published OrderFailedNotificationEvent: {}", event);
        });
    }
}
