package com.phumlanidev.orderservice.repository;


import com.phumlanidev.orderservice.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Comment: this is the placeholder for documentation.
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
