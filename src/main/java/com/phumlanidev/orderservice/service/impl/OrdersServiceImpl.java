package com.phumlanidev.orderservice.service.impl;


import com.phumlanidev.commonevents.events.order.OrderPlacedEvent;
import com.phumlanidev.commonevents.events.order.OrderPlacedEvent.OrderItemDto;
import com.phumlanidev.orderservice.constant.Constant;
import com.phumlanidev.orderservice.dto.CartDto;
import com.phumlanidev.orderservice.dto.OrderDto;
import com.phumlanidev.orderservice.enums.OrderStatus;
import com.phumlanidev.orderservice.event.publiser.OrderPlacedEventPublisher;
import com.phumlanidev.orderservice.exception.order.OrderNotFoundException;
import com.phumlanidev.orderservice.model.Order;
import com.phumlanidev.orderservice.model.OrderItem;
import com.phumlanidev.orderservice.repository.OrderRepository;
import com.phumlanidev.orderservice.service.IOrdersService;
import com.phumlanidev.orderservice.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrdersServiceImpl implements IOrdersService {

  private final OrderRepository orderRepository;
  private final CartServiceClient cartServiceClient;
  private final AuditLogServiceImpl auditLogService;
  private final OrderPlacedEventPublisher orderPlacedEventPublisher;
  private final SecurityUtils securityUtils;

  @Override
  public void placeOrder(String userId) {
    String token = securityUtils.getCurrentAuthorizationHeader();

    CartDto cart = cartServiceClient.getCart(userId, token);

    if (cart.getCartItems().isEmpty()) {
      throw new IllegalArgumentException("Cannot place order with empty cart");
    }

    Order order = Order.builder()
            .userId(userId)
            //.totalPrice(totalPrice)
            .orderStatus(OrderStatus.PLACED)
            .items(new ArrayList<>())
            .createdBy(userId)
            .build();

    List<OrderItem> orderItems = cart.getCartItems().stream()
            .map(item -> OrderItem.builder()
                    .order(order)
                    .productId(item.getProductId())
                    .quantity(item.getQuantity())
                    .priceAtPurchase(item.getPrice())
                    .build())
            .toList();

    order.setItems(orderItems);

    BigDecimal totalPrice = orderItems.stream()
            .map(item -> {
              BigDecimal price = item.getPriceAtPurchase() != null
                      ? item.getPriceAtPurchase() : BigDecimal.ZERO;
              return price.multiply(BigDecimal.valueOf(item.getQuantity()));
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    order.setTotalPrice(totalPrice);

    orderRepository.save(order);

    logAudit("ORDER_PLACED", "Order placed successfully for user: " + userId);
    String emailRecipient = securityUtils.getCurrentEmail();

    OrderPlacedEvent event = OrderPlacedEvent.builder()
            .userId(userId)
            .orderId(order.getOrderId())
            .total(totalPrice)
            .toEmail(emailRecipient) // Assuming username is the email
            .timestamp(Instant.now())
            .items(orderItems.stream()
                    .map(item -> OrderItemDto.builder()
                            .productId(item.getProductId())
                            .quantity(item.getQuantity())
                            .build())
                    .collect(Collectors.toList()))
            .build();

    log.debug("Sending order notification for userId: {}, orderId: {}",
            event.getUserId(), event.getOrderId());

    orderPlacedEventPublisher.publishOrderPlacedEvent(event);
  }

  @Override
  public List<OrderDto> getUserOrders(String userId) {
    String currentUserId = securityUtils.getCurrentUserId();
    List<Order> orders = orderRepository.findByUserId(currentUserId);
    if (orders.isEmpty()) {
      log.warn("No orders found for user: {}", userId);
      return List.of();
    }
    log.info("Found {} orders for user: {}", orders.size(), userId);
    logAudit("USER_ORDERS_RETRIEVED", "Retrieved orders for user: " + userId);
    return orders.stream()
            .map(order -> OrderDto.builder()
                    .orderId(order.getOrderId())
                    .userId(String.valueOf(order.getUserId()))
                    .totalPrice(order.getTotalPrice())
                    .items(order.getItems().stream()
                            .map(item -> OrderItem.builder()
                                    .productId(item.getProductId())
                                    .quantity(item.getQuantity())
                                    .priceAtPurchase(item.getPriceAtPurchase())
                                    .build())
                            .toList())
                    .build())
            .toList();
  }

  @Override
  public List<OrderDto> getAllOrders() {
    List<Order> orders = orderRepository.findAll();
    if (!orders.isEmpty()) {
      log.info("Found {} orders in the system", orders.size());
      logAudit("ALL_ORDERS_RETRIEVED", "Retrieved all orders");
      return orders.stream()
              .map(order -> OrderDto.builder()
                      .orderId(order.getOrderId())
                      .userId(String.valueOf(order.getUserId()))
                      .totalPrice(order.getTotalPrice())
                      .items(order.getItems().stream()
                              .map(item -> OrderItem.builder()
                                      .productId(item.getProductId())
                                      .quantity(item.getQuantity())
                                      .priceAtPurchase(item.getPriceAtPurchase())
                                      .build())
                              .toList())
                      .build())
              .toList();
    } else {
      log.warn("No orders found in the system");
      return List.of();
    }
  }

  @Override
  public void markOrderAsPaid(Long orderId) {
    Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException(Constant.ORDER_NOT_FOUND));

    order.setOrderStatus(OrderStatus.PAID);
    order.setUpdatedAt(Instant.now());
    order.setUpdatedBy("SYSTEM"); // Assuming system update
    orderRepository.save(order);
    log.info("2. ✅ Order {} status changed to PAID", orderId);
    logAudit("ORDER_PAID", "Order marked as paid: " + orderId);

  }

  @Override
  public OrderDto getOrderById(Long orderId) {
    Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException("Order not found"));

    OrderDto orderDto = OrderDto.builder()
            .orderId(order.getOrderId())
            .userId(String.valueOf(order.getUserId()))
            .totalPrice(order.getTotalPrice())
            .items(order.getItems().stream()
                    .map(item -> OrderItem.builder()
                            .productId(item.getProductId())
                            .quantity(item.getQuantity())
                            .priceAtPurchase(item.getPriceAtPurchase())
                            .build())
                    .toList())
            .build();
    logAudit("ORDER_RETRIEVED", "Order retrieved successfully for orderId: " + orderId);
    return orderDto;
  }

  @Override
  public void cancelOrder(Long orderId) {
    Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException("Order not found"));

    if (order.getOrderStatus() == OrderStatus.CANCELLED) {
      log.warn("Order {} is already cancelled", orderId);
      return;
    }

    order.setOrderStatus(OrderStatus.CANCELLED);
    order.setUpdatedAt(Instant.now());
    order.setUpdatedBy("SYSTEM"); // Assuming system update
    orderRepository.save(order);
    log.info("Order {} has been cancelled", orderId);
    logAudit("ORDER_CANCELLED", "Order cancelled successfully for orderId: " + orderId);
  }

  private void logAudit(String action, String details) {
    String clientIp = securityUtils.getCurrentClientIp();
    String username = securityUtils.getCurrentUsername();
    String userId = securityUtils.getCurrentUserId();

    auditLogService.log(
            action,
            userId,
            username,
            clientIp,
            details
    );
  }
}
