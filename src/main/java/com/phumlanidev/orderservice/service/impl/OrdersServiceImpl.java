package com.phumlanidev.orderservice.service.impl;

import com.phumlanidev.orderservice.client.NotificationClient;
import com.phumlanidev.orderservice.config.JwtAuthenticationConverter;
import com.phumlanidev.orderservice.dto.CartDto;
import com.phumlanidev.orderservice.dto.OrderDto;
import com.phumlanidev.orderservice.dto.OrderNotifyRequestDto;
import com.phumlanidev.orderservice.enums.OrderStatus;
import com.phumlanidev.orderservice.event.NotificationEvetPublisher;
import com.phumlanidev.orderservice.exception.order.OrderNotFoundException;
import com.phumlanidev.orderservice.model.Order;
import com.phumlanidev.orderservice.model.OrderItem;
import com.phumlanidev.orderservice.repository.OrderRepository;
import com.phumlanidev.orderservice.service.IOrdersService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrdersServiceImpl implements IOrdersService {

  private final OrderRepository orderRepository;
  private final CartServiceClient cartServiceClient;
  private final NotificationEvetPublisher evetPublisher;
  private final HttpServletRequest request;
  private final AuditLogServiceImpl auditLogService;
  private final JwtAuthenticationConverter jwtAuthenticationConverter;
  private final NotificationClient notificationClient;

  @Override
  public void placeOrder(String userId) {
    String token = request.getHeader("Authorization"); // Remove "Bearer " prefix

    if (token == null || !token.startsWith("Bearer ")) {
      throw new IllegalArgumentException("Invalid or missing Authorization header");
    }

    CartDto cart = cartServiceClient.getCart(userId, token);

    if (cart.getCartItems().isEmpty()) {
      throw new IllegalArgumentException("Cannot place order with empty cart");
    }

    Order order = Order.builder()
            .userId(userId)
            //.totalPrice(totalPrice)
            .orderStatus(OrderStatus.PLACED)
            .items(new ArrayList<>())
            .createdAt(LocalDateTime.now())
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
    Jwt jwt = jwtAuthenticationConverter.getJwt();
    String emailRecipient = jwtAuthenticationConverter.extractUserEmail(jwt);

    OrderNotifyRequestDto notification = OrderNotifyRequestDto.builder()
            .userId(userId)
            .orderId(order.getOrderId())
            .total(totalPrice)
            .toEmail(emailRecipient) // Assuming username is the email
            .timestamp(Instant.now())
            .build();

    log.debug("Sending order notification for userId: {}, orderId: {}",
              notification.getUserId(), notification.getOrderId());

    notificationClient.orderNotifyPlaced(notification, jwt);

    evetPublisher.publishOrderPlaced(userId, order);
  }

  @Override
  public List<OrderDto> getUserOrders(String userId) {
    List<Order> orders = orderRepository.findByUserId(userId);
    if (orders.isEmpty()) {
      log.warn("No orders found for user: {}", userId);
      return List.of();
    }
    log.info("Found {} orders for user: {}", orders.size(), userId);
    logAudit("USER_ORDERS_RETRIEVED", "Retrieved orders for user: " + userId);
    return orders.stream()
            .map(order -> OrderDto.builder()
                    .orderId(order.getOrderId())
                    .userId(order.getUserId())
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
                      .userId(order.getUserId())
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
            .orElseThrow(() -> new IllegalArgumentException("Order not found"));

    order.setOrderStatus(OrderStatus.PAID);
    order.setUpdatedAt(LocalDateTime.now());
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
            .userId(order.getUserId())
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

  private void logAudit(String action, String details) {
    String clientIp = request.getRemoteAddr();
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String username = auth != null ? auth.getName() : "anonymous";
    Jwt jwt = jwtAuthenticationConverter.getJwt();
    String userId = jwtAuthenticationConverter.extractUserId(jwt);


    auditLogService.log(
            action,
            userId,
            username,
            clientIp,
            details
    );
  }
}
