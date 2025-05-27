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


  //  /**
  //   * Comment: this is the placeholder for documentation.
  //   */
  //  OrderDto getOrderDetails(Long orderId);
  //
  //
  //
  //  /**
  //   * Comment: this is the placeholder for documentation.
  //   */
  //  OrderDto updateOrderStatus(Long orderId, String status);
  //
  //  /**
  //   * Comment: this is the placeholder for documentation.
  //   */
  //  void cancelOrder(Long orderId);

}
