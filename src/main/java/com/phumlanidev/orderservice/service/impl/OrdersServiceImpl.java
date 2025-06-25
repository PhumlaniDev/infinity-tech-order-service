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

    String clientIp = request.getRemoteAddr();
    Jwt jwt = null;
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String username = "anonymous";
    String emailRecipient = null;
    if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof Jwt j) {
      jwt = j;
      userId = jwt.getSubject(); // Keycloak userId (UUID)
      username = jwt.getSubject(); // Keycloak username
      emailRecipient = jwtAuthenticationConverter.extractUserEmail(jwt);
    }

    auditLogService.log(
            "ORDER_PLACED",
            userId,
            username,
            clientIp,
            "Order placed successfully");

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
    return List.of();
  }

  @Override
  public List<OrderDto> getAllOrders() {
    return List.of();
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

  }

  @Override
  public OrderDto getOrderById(Long orderId) {
    Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException("Order not found"));

    return OrderDto.builder()
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
  }
}
