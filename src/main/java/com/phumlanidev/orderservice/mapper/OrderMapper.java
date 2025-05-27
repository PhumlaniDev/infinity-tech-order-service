package com.phumlanidev.orderservice.mapper;


import com.phumlanidev.orderservice.dto.OrderDto;
import com.phumlanidev.orderservice.model.Order;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Comment: this is the placeholder for documentation.
 */
@Component
@RequiredArgsConstructor
public class OrderMapper {

  /**
   * Comment: this is the placeholder for documentation.
   */

  public Order toEntity(OrderDto dto, Order order) {


    return order;
  }

  /**
   * Comment: this is the placeholder for documentation.
   */

  public OrderDto toDto(Order order, OrderDto dto) {

    return dto;
  }
}
