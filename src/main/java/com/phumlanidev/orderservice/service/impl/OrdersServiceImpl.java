package com.phumlanidev.orderservice.service.impl;

import com.phumlanidev.orderservice.dto.CartDto;
import com.phumlanidev.orderservice.dto.OrderDto;
import com.phumlanidev.orderservice.enums.OrderStatus;
import com.phumlanidev.orderservice.event.NotificationEvetPublisher;
import com.phumlanidev.orderservice.mapper.OrderMapper;
import com.phumlanidev.orderservice.model.Order;
import com.phumlanidev.orderservice.model.OrderItem;
import com.phumlanidev.orderservice.repository.OrderRepository;
import com.phumlanidev.orderservice.service.IOrdersService;
import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

/**
 * Comment: this is the placeholder for documentation.
 */
@Service
@RequiredArgsConstructor
public class OrdersServiceImpl implements IOrdersService {

  private final OrderRepository orderRepository;
  private final CartServiceClient cartServiceClient;
  private final NotificationEvetPublisher evetPublisher;
  private final HttpServletRequest request;
  private final AuditLogServiceImpl auditLogService;
  private final OrderMapper orderMapper;


  /**
   * Comment: this is the placeholder for documentation.
   * return
   */
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
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String username = auth != null ? auth.getName() : "anonymous";

    if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
      userId = jwt.getSubject(); // Keycloak userId (UUID)
    }

    auditLogService.log(
            "ORDER_PLACED",
            userId,
            username,
            clientIp,
            "Order placed successfully");

    evetPublisher.publishOrderPlaced(userId, order);
  }

  /**
   * Comment: this is the placeholder for documentation.
   */
  @Override
  public List<OrderDto> getUserOrders(String userId) {
    return List.of();
  }

  /**
   * Comment: this is the placeholder for documentation.
   */
  @Override
  public List<OrderDto> getAllOrders() {
    return List.of();
  }
}
