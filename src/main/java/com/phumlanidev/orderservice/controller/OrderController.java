package com.phumlanidev.orderservice.controller;

import com.phumlanidev.orderservice.service.impl.OrdersServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Comment: this is the placeholder for documentation.
 */
@RestController
@RequestMapping(path = "/api/v1/order")
@RequiredArgsConstructor
@Validated
public class OrderController {

  private final OrdersServiceImpl ordersService;

  /**
   * Comment: this is the placeholder for documentation.
   */
  @PostMapping("/place-order")
  public ResponseEntity<Void> placeOrder(@AuthenticationPrincipal Jwt jwt) {
    ordersService.placeOrder(jwt.getSubject());
    return ResponseEntity.ok().build();
  }

  //  @GetMapping("/{orderId}")
  //  public ResponseEntity<OrderDto> getOrderDetails(@PathVariable Long orderId) {
  ////    OrderDto orderDto = ordersService.getOrderDetails(orderId);
  ////    return ResponseEntity.ok(orderDto);
  //  }
  //
  //  /**
  //   * Comment: this is the placeholder for documentation.
  //   */
  //  @GetMapping("/user/{userId}")
  //  public ResponseEntity<List<OrderDto>> getUserOrders(@PathVariable Long userId) {
  ////    List<OrderDto> orders = ordersService.getUserOrders(userId);
  ////    return ResponseEntity.ok(orders);
  //  }
  //
  //  /**
  //   * Comment: this is the placeholder for documentation.
  //   */
  //  @PutMapping("/{orderId}/status")
  //  public ResponseEntity<OrderDto> updateOrderStatus(
  //      @PathVariable Long orderId,
  //      @RequestParam String status) {
  ////    OrderDto updatedOrder = ordersService.updateOrderStatus(orderId, status);
  ////    return ResponseEntity.ok(updatedOrder);
  //  }
  //
  //  /**
  //   * Comment: this is the placeholder for documentation.
  //   */
  //  @DeleteMapping("/{orderId}/cancel")
  //  public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
  ////    ordersService.cancelOrder(orderId);
  ////    return ResponseEntity.noContent().build();
  //  }
}
