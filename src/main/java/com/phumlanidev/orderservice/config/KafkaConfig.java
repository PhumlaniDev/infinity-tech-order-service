package com.phumlanidev.orderservice.config;


import com.phumlanidev.commonevents.events.StockReservationFailedEvent;
import com.phumlanidev.commonevents.events.StockReservedEvent;
import com.phumlanidev.commonevents.events.order.OrderFailedNotificationEvent;
import com.phumlanidev.commonevents.events.order.OrderPlacedEvent;
import com.phumlanidev.commonevents.events.payment.PaymentCompletedEvent;
import com.phumlanidev.commonevents.events.payment.PaymentRequestEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

  @Value("${spring.kafka.bootstrap-servers:localhost:29092}")
  private String bootstrapServers;

  @Bean
  public ProducerFactory<String, PaymentRequestEvent> paymentRequestEventProducerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    props.put("spring.json.add.type.headers", true); // Include type headers for deserialization
    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean
  public KafkaTemplate<String, PaymentRequestEvent> paymentRequestEventKafkaTemplate() {
    return new KafkaTemplate<>(paymentRequestEventProducerFactory());
  }

  @Bean
  public ProducerFactory<String, OrderPlacedEvent> orderPlacedEventProducerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
//    props.put("spring.json.add.type.headers", true); // Include type headers for deserialization
    props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, true);
    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean
  public KafkaTemplate<String, OrderPlacedEvent> orderPlacedEventKafkaTemplate() {
    return new KafkaTemplate<>(orderPlacedEventProducerFactory());
  }

  @Bean
  public ProducerFactory<String, OrderFailedNotificationEvent> orderFailedNotificationEventProducerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    props.put("spring.json.add.type.headers", true); // Include type headers for deserialization
    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean
  public KafkaTemplate<String, OrderFailedNotificationEvent> orderFailedNotificationEventKafkaTemplate() {
    return new KafkaTemplate<>(orderFailedNotificationEventProducerFactory());
  }

  @Bean
  public ProducerFactory<String, StockReservationFailedEvent> stockReservedFailedEventProducerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, true);
    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean
  public KafkaTemplate<String, StockReservationFailedEvent> stockReservationFailedEventKafkaTemplate() {
    return new KafkaTemplate<>(stockReservedFailedEventProducerFactory());
  }

  @Bean
  public ConsumerFactory<String, StockReservedEvent> stockReservedEventConsumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "order-group");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
    props.put("spring.deserializer.key.delegate.class", StringDeserializer.class);
    props.put("spring.deserializer.value.delegate.class", JsonDeserializer.class);
    props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.phumlanidev.commonevents.events");
    props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false); // Use type headers for deserialization
    props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "com.phumlanidev.commonevents.events.StockReservedEvent");
    return new DefaultKafkaConsumerFactory<>(props);
  }

  @Bean(name = "stockReservedEventConsumerContainerFactory")
  public ConcurrentKafkaListenerContainerFactory<String, StockReservedEvent> stockReservedEventConsumerContainerFactory(
          DefaultErrorHandler errorHandler
  ) {
    ConcurrentKafkaListenerContainerFactory<String, StockReservedEvent> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(stockReservedEventConsumerFactory());
    factory.setConcurrency(3); // Match your existing event concurrency
    factory.getContainerProperties().setAckMode(org.springframework.kafka.listener.ContainerProperties.AckMode.MANUAL);
    factory.setCommonErrorHandler(errorHandler);
    return factory;
  }

  @Bean
  public ConsumerFactory<String, PaymentCompletedEvent> paymentCompletedEventConsumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "order-group");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
    props.put("spring.deserializer.key.delegate.class", StringDeserializer.class);
    props.put("spring.deserializer.value.delegate.class", JsonDeserializer.class);
    props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.phumlanidev.commonevents.events");
    props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, true); // Use type headers for deserialization
    props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "com.phumlanidev.commonevents.events.PaymentCompletedEvent");
    return new DefaultKafkaConsumerFactory<>(props);
  }

  @Bean(name = "paymentCompletedEventConsumerContainerFactory")
  public ConcurrentKafkaListenerContainerFactory<String, PaymentCompletedEvent> paymentCompletedEventConsumerContainerFactory(
          DefaultErrorHandler errorHandler
  ) {
    ConcurrentKafkaListenerContainerFactory<String, PaymentCompletedEvent> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(paymentCompletedEventConsumerFactory());
    factory.setConcurrency(3); // Match your existing event concurrency
    factory.getContainerProperties().setAckMode(org.springframework.kafka.listener.ContainerProperties.AckMode.MANUAL);
    factory.setCommonErrorHandler(errorHandler);
    return factory;
  }

  @Bean
  public ConsumerFactory<String, StockReservationFailedEvent> stockReservedFailedEventConsumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "order-group");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
    props.put("spring.deserializer.key.delegate.class", StringDeserializer.class);
    props.put("spring.deserializer.value.delegate.class", JsonDeserializer.class);
    props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.phumlanidev.commonevents.events");
    props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, true); // Use type headers for deserialization
    props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, StockReservationFailedEvent.class);
    return new DefaultKafkaConsumerFactory<>(props);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, StockReservationFailedEvent> stockReservedFailedEventContainerFactory(
          DefaultErrorHandler errorHandler
  ) {
    ConcurrentKafkaListenerContainerFactory<String, StockReservationFailedEvent> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(stockReservedFailedEventConsumerFactory());
    factory.setConcurrency(3); // Match your existing event concurrency
    factory.getContainerProperties().setAckMode(org.springframework.kafka.listener.ContainerProperties.AckMode.MANUAL);
    factory.setCommonErrorHandler(errorHandler);
    return factory;
  }

  @Bean
  public ProducerFactory<String, StockReservedEvent> stockReservedEventProducerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    props.put("spring.json.add.type.headers", true);
    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean
  public KafkaTemplate<String, StockReservedEvent> stockReservedEventKafkaTemplate() {
    return new KafkaTemplate<>(stockReservedEventProducerFactory());
  }

//  @Bean
//  public KafkaTemplate<String, PaymentCompletedEvent> paymentEventConsumberKafkaTemplate() {
//    return new KafkaTemplate<>(stockReservedEventProducerFactory());
//  }

  @Bean
  public ProducerFactory<Object, Object> genericProducerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, true);
    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean
  public KafkaTemplate<Object, Object> kafkaTemplate() {
    return new KafkaTemplate<>(genericProducerFactory());
  }

  @Bean
  public DefaultErrorHandler defaultErrorHandler(KafkaTemplate<Object, Object> kafkaTemplate) {
    DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
            kafkaTemplate,
            (record, ex) -> {
              String dlqTopic = record.topic() + ".DLT"; // dead-letter topic convention
              return new TopicPartition(dlqTopic, record.partition());
            });

    // Retry 3 times with 1s delay, doubling each time
    FixedBackOff backOff = new FixedBackOff(1000L, 3);

    DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff);
    errorHandler.setRetryListeners((record, ex, deliveryAttempt) ->
            System.out.printf("❌ Retry %d for record %s due to %s%n", deliveryAttempt, record, ex.getMessage()));

    return errorHandler;
  }
}
