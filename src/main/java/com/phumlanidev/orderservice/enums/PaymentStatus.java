package com.phumlanidev.orderservice.enums;

/**
 * Comment: this is the placeholder for documentation.
 */
public enum PaymentStatus {

  PENDING,       // Payment has been initiated but not completed
  COMPLETED,     // Payment has been successfully processed
  FAILED,        // Payment attempt was unsuccessful
  REFUNDED,      // Payment has been returned to the customer
  PARTIALLY_REFUNDED, // Part of the payment has been returned to the customer
  CANCELLED      // Payment has been cancelled
}
