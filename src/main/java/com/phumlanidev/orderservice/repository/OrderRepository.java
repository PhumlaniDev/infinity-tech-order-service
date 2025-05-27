package com.phumlanidev.orderservice.repository;

import com.phumlanidev.orderservice.model.Order;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Comment: this is the placeholder for documentation.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

  /**
   * Comment: this is the placeholder for documentation.
   */
  List<Order> findByUserId(String userId);

}
