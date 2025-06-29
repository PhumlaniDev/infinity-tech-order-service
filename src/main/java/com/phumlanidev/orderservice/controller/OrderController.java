package com.phumlanidev.orderservice.controller;

import com.phumlanidev.orderservice.dto.OrderDto;
import com.phumlanidev.orderservice.service.impl.OrdersServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Comment: this is the placeholder for documentation.
 */
@RestController
@RequestMapping(path = "/api/v1/order")
@RequiredArgsConstructor
@Validated
public class OrderController {

  private final OrdersServiceImpl ordersService;

  @GetMapping("/all")
  public ResponseEntity<List<OrderDto>> getAllOrders() {
    List<OrderDto> orders = ordersService.getAllOrders();
    return ResponseEntity.ok(orders);
  }

  @GetMapping("/health")
  public ResponseEntity<String> healthCheck() {
    return ResponseEntity.ok("Order service is up and running!");
  }

  @PostMapping("/place-order")
  public ResponseEntity<Void> placeOrder(@Valid @AuthenticationPrincipal Jwt jwt) {
    ordersService.placeOrder(jwt.getSubject());
    return ResponseEntity.ok().build();
  }

  @PutMapping("/mark-paid/{orderId}")
  public ResponseEntity<Void> markOrderAsPaid(@PathVariable Long orderId) {
    ordersService.markOrderAsPaid(orderId);
    return ResponseEntity.ok().build();
  }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> getOrderDetails(@PathVariable Long orderId) {
      OrderDto orderDto = ordersService.getOrderById(orderId);
      return ResponseEntity.ok(orderDto);
    }

  //  /**
  //   * Comment: this is the placeholder for documentation.
  //   */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDto>> getUserOrders(@PathVariable String userId) {
      List<OrderDto> orders = ordersService.getUserOrders(userId);
      return ResponseEntity.ok(orders);
    }

  @DeleteMapping("/{orderId}/cancel")
  public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
    ordersService.cancelOrder(orderId);
    return ResponseEntity.noContent().build();
  }
}
