package com.phumlanidev.orderservice.service;

import com.phumlanidev.orderservice.dto.OrderDto;

import java.util.List;

/**
 * Comment: this is the placeholder for documentation.
 */
public interface IOrdersService {

  /**
   * Comment: this is the placeholder for documentation.
   * return
   */
  void placeOrder(String userId);

  /**
   * Comment: this is the placeholder for documentation.
   */
  List<OrderDto> getUserOrders(String userId);

  /**
   * Comment: this is the placeholder for documentation.
   */
  List<OrderDto> getAllOrders();

  void markOrderAsPaid(Long orderId);

  OrderDto getOrderById(Long orderId);

  //  OrderDto updateOrderStatus(Long orderId, String status);

    void cancelOrder(Long orderId);

}
