package com.phumlanidev.orderservice.service.impl;

import com.phumlanidev.orderservice.dto.CartDto;
import com.phumlanidev.orderservice.dto.CartItemDto;
import com.phumlanidev.orderservice.dto.OrderDto;
import com.phumlanidev.orderservice.enums.OrderStatus;
import com.phumlanidev.orderservice.exception.order.OrderNotFoundException;
import com.phumlanidev.orderservice.model.Order;
import com.phumlanidev.orderservice.model.OrderItem;
import com.phumlanidev.orderservice.repository.OrderRepository;
import com.phumlanidev.orderservice.utils.NotificationServiceWrapper;
import com.phumlanidev.orderservice.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
class OrdersServiceImplTest {

  @InjectMocks
  private OrdersServiceImpl ordersServiceImpl;

  @Mock private OrderRepository orderRepository;
  @Mock private CartServiceClient cartServiceClient;
  @Mock private NotificationServiceWrapper notificationServiceWrapper;
  @Mock private AuditLogServiceImpl auditLogService;
  @Mock private SecurityUtils securityUtils;

  @Test
  void shouldPlaceOrderSuccessfully() {
    String userId = "user-123";
    String email = "aphumlani@gmail.com";
    String username = "aphumlani";
    String clientIp = "192.168.0.1";
    String token = "Bearer some.jwt.token";

    // 1. Mock the necessary dependencies and their behaviors
    when(securityUtils.getCurrentToken()).thenReturn(token);
    when(securityUtils.getCurrentEmail()).thenReturn(email);
    when(securityUtils.getCurrentUserId()).thenReturn(userId);
    when(securityUtils.getCurrentClientIp()).thenReturn(clientIp);
    when(securityUtils.getCurrentUsername()).thenReturn(username);

    // 2. Prepare the CartDto with items
    CartDto cartDto = new CartDto();
    cartDto.setCartItems(List.of(new CartItemDto(1L, 2, null)));

    when(cartServiceClient.getCart(userId, token)).thenReturn(cartDto);

    // 3. Save Order
    Order order = Order.builder().orderId(99L).userId(userId).build();
    when(orderRepository.save(any(Order.class))).thenReturn(order);

    // 4. Call the method under test
    ordersServiceImpl.placeOrder(userId);

    // 5. Verify interactions and assertions
    verify(orderRepository).save(any(Order.class));
    verify(notificationServiceWrapper).sendOrderPlacedNotification(any());
    verify(auditLogService).log("ORDER_PLACED",
            userId,
            username,
           clientIp,
            "Order placed successfully for user: " + userId
    );
  }

  @Test
  void shouldGetUsersOrders() {
    String userId = "user-123";
    String username = "aphumlani";
    String clientIp = "192.168.0.2";

    // 1. Mock the necessary dependencies and their behaviors
    when(securityUtils.getCurrentUserId()).thenReturn(userId);
    when(securityUtils.getCurrentClientIp()).thenReturn(clientIp);
    when(securityUtils.getCurrentUsername()).thenReturn(username);

    // 2. Prepare the Order list
    OrderItem orderItem = new OrderItem();
    List<Order> orders = List.of(
            Order.builder()
                    .orderId(1L)
                    .userId(userId)
                    .items(Stream.of(orderItem)
                            .map(
                                    item -> OrderItem.builder()
                                            .productId(1L)
                                            .quantity(3)
                                            .priceAtPurchase(BigDecimal.valueOf(100))
                                            .build()
                            ).toList()).build(),
            Order.builder()
                    .orderId(2L)
                    .userId(userId)
                    .items(Stream.of(orderItem)
                            .map(
                                    item -> OrderItem.builder()
                                            .productId(2L)
                                            .quantity(3)
                                            .priceAtPurchase(BigDecimal.valueOf(100))
                                            .build()
                            ).toList()).build(),
            Order.builder()
                    .orderId(3L)
                    .userId(userId)
                    .items(Stream.of(orderItem)
                            .map(
                                    item -> OrderItem.builder()
                                            .productId(3L)
                                            .quantity(3)
                                            .priceAtPurchase(BigDecimal.valueOf(100))
                                            .build()
                            ).toList()).build()
    );

    when(orderRepository.findByUserId(userId)).thenReturn(orders);

    // 3. Call the method under test
    ordersServiceImpl.getUserOrders(userId);

    verify(orderRepository).findByUserId(userId);
    verify(auditLogService).log(
            "USER_ORDERS_RETRIEVED",
            userId,
            username,
            clientIp,
            "Retrieved orders for user: " + userId);
  }

  @Test
  void shouldGetAllOrders() {
    String userId = "user-123";
    String username = "aphumlani";
    String clientIp = "192.168.0.3";

    // 1. Mock the necessary dependencies and their behaviors
    when(securityUtils.getCurrentUserId()).thenReturn(userId);
    when(securityUtils.getCurrentUsername()).thenReturn(username);
    when(securityUtils.getCurrentClientIp()).thenReturn(clientIp);

    OrderItem orderItem = new OrderItem();
    // 2. Prepare the Order list
    List<Order> orders = List.of(
            Order.builder()
                    .orderId(1L)
                    .userId(userId)
                    .items(Stream.of(orderItem)
                            .map(
                            item -> OrderItem.builder()
                                    .productId(1L)
                                    .quantity(3)
                                    .priceAtPurchase(BigDecimal.valueOf(100))
                                    .build()
                    ).toList()).build(),
            Order.builder()
                    .orderId(2L)
                    .userId(userId)
                    .items(Stream.of(orderItem)
                            .map(
                                    item -> OrderItem.builder()
                                            .productId(2L)
                                            .quantity(3)
                                            .priceAtPurchase(BigDecimal.valueOf(100))
                                            .build()
                            ).toList()).build(),
            Order.builder()
                    .orderId(3L)
                    .userId(userId)
                    .items(Stream.of(orderItem)
                            .map(
                                    item -> OrderItem.builder()
                                            .productId(3L)
                                            .quantity(3)
                                            .priceAtPurchase(BigDecimal.valueOf(100))
                                            .build()
                            ).toList()).build()
    );
    when(orderRepository.findAll()).thenReturn(orders);

    // 3. Call Method under test
    ordersServiceImpl.getAllOrders();

    // 4. Verify interactions and assertions
    verify(orderRepository).findAll();
    verify(auditLogService).log(
            "ALL_ORDERS_RETRIEVED",
            userId,
            username,
            clientIp,
           "Retrieved all orders"
  );

  }

  @Test
  void shouldMarkOrderPaid() {
    String userId = "user-123";
    String username = "aphumlani";
    String clientIp = "192.168.0.4";
    Long orderId = 1L;

    // 1. Mock dependencies
    when(securityUtils.getCurrentUserId()).thenReturn(userId);
    when(securityUtils.getCurrentUsername()).thenReturn(username);
    when(securityUtils.getCurrentClientIp()).thenReturn(clientIp);

    // 2. Prepare the order to mark PAID
    Order order = Order.builder()
            .orderId(orderId)
            .orderStatus(OrderStatus.PAID)
            .userId(userId)
            .updatedAt(Instant.now()).build();
    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
    when(orderRepository.save(any(Order.class))).thenReturn(order);

    // 3.Call method under test
    ordersServiceImpl.markOrderAsPaid(orderId);

    // 4. Verify interaction and assertions
    verify(orderRepository).save(any(Order.class));
  }

  @Test
  void shouldGetOrderById() {
    String userId = "user-123";
    String username = "aphumlani";
    String clientIp = "19.168.0.5";
    Long orderId = 1L;

    // 1. Mock dependencies
    when(securityUtils.getCurrentUserId()).thenReturn(userId);
    when(securityUtils.getCurrentUsername()).thenReturn(username);
    when(securityUtils.getCurrentClientIp()).thenReturn(clientIp);

    // 2. Prepare the order
    OrderItem orderItem = new OrderItem();
    Order order = Order.builder()
            .orderId(orderId)
            .orderStatus(OrderStatus.PAID)
            .items(Stream.of(orderItem)
                    .map(
                            item -> OrderItem.builder()
                                    .productId(3L)
                                    .quantity(3)
                                    .priceAtPurchase(BigDecimal.valueOf(100))
                                    .build()
                    ).toList())
            .userId(userId)
            .updatedAt(Instant.now()).build();
    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

    // 3. Call method under test
    ordersServiceImpl.getOrderById(orderId);

    // 4. Verify interaction and assertions
    verify(orderRepository).findById(orderId);
    verify(auditLogService).log(
            "ORDER_RETRIEVED",
            userId,
            username,
            clientIp,
            "Order retrieved successfully for orderId: " + orderId
    );
  }

  @Test
  void shouldCancelSuccessfully() {
    String userId = "user-1230";
    String username = "aphumlani";
    String clientIp = "192.168.0.6";
    Long orderId = 1L;

    // 1. Mock dependencies
    when(securityUtils.getCurrentUserId()).thenReturn(userId);
    when(securityUtils.getCurrentUsername()).thenReturn(username);
    when(securityUtils.getCurrentClientIp()).thenReturn(clientIp);

    // 2. Prepare order to be cancelled
    Order order = Order.builder()
            .orderId(orderId)
            .userId(userId)
            .orderStatus(OrderStatus.PLACED)
            .updatedAt(Instant.now())
            .updatedBy("SYSTEM").build();
    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
    when(orderRepository.save(any(Order.class))).thenReturn(order);

    // 4. Call the method under test
    ordersServiceImpl.cancelOrder(orderId);

    // 5.Verify interactions and assertions
    verify(orderRepository).save(any(Order.class));
    verify(auditLogService).log(
            "ORDER_CANCELLED",
            userId,
            username,
            clientIp,
            "Order cancelled successfully for orderId: " + orderId
    );

  }

  @Test
  void shouldThrowAnExceptionWhenCartIsEmpty() {
    String token = "Bearer some.jwt.token";
    String userId = "user-123";

    // Mock dependencies
    when(securityUtils.getCurrentToken()).thenReturn(token);

    //Prepare CartDto with items
    CartDto emptyCart = new CartDto();
    emptyCart.setCartItems(Collections.emptyList());
    when(cartServiceClient.getCart(userId, token)).thenReturn(emptyCart);

    // Call method under test
    IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ordersServiceImpl.placeOrder(userId)
    );

    assertEquals("Cannot place order with empty cart", exception.getMessage());

    // Verify interactions and assertions
    verify(orderRepository, never()).save(any());
    verify(notificationServiceWrapper, never()).sendOrderPlacedNotification(any());
  }

  @Test
  void shouldNotProceedIfOrderIsCancelled() {
    Long orderId = 1L;
    Order cancelledOrder = Order.builder()
            .orderId(orderId)
            .orderStatus(OrderStatus.CANCELLED)
            .build();

    when(orderRepository.findById(orderId)).thenReturn(Optional.of(cancelledOrder));

    ordersServiceImpl.cancelOrder(orderId);

    verify(orderRepository, never()).save(any());
    // Optionally, use a log-capturing utility to verify the log message if needed
  }

  @Test
  void shouldReturnEmptyListAndLogWarningIfNoOrdersInSystem() {
    when(orderRepository.findAll()).thenReturn(List.of());

    List<OrderDto> result = ordersServiceImpl.getAllOrders();

    assertTrue(result.isEmpty());
    verify(orderRepository).findAll();
    // Optional: capture and assert the log
  }

  @Test
  void shouldProcessOrdersWhenListIsNotEmpty() {
    Order order = Order.builder().orderId(1L).userId("user-1").build();
    when(orderRepository.findAll()).thenReturn(List.of(order));

    List<OrderDto> result = ordersServiceImpl.getAllOrders();

    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
    assertEquals("user-1", result.getFirst().getUserId());
  }

  @Test
  void shouldReturnEmptyListWhenUserHasNoOrders() {
    String userId = "user-123";

    when(securityUtils.getCurrentUserId()).thenReturn(userId);
    when(orderRepository.findByUserId(userId)).thenReturn(List.of());

    List<OrderDto> result = ordersServiceImpl.getUserOrders(userId);

    assertTrue(result.isEmpty());
    verify(orderRepository).findByUserId(userId);
  }

  @Test
  void shouldThrowExceptionWhenOrderNotFound() {
    Long orderId = 999L;

    when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

    OrderNotFoundException orderNotFoundException = assertThrows(
            OrderNotFoundException.class,
            () -> ordersServiceImpl.getOrderById(orderId)
    );

    assertEquals("Order not found", orderNotFoundException.getMessage());

    verify(orderRepository).findById(orderId);
    verify(auditLogService, never()).log(
            anyString(), anyString(), anyString(), anyString(), anyString()
    );
  }

}