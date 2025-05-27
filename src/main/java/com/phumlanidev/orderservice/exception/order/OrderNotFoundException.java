package com.phumlanidev.orderservice.exception.order;


import com.phumlanidev.orderservice.exception.BaseException;
import org.springframework.http.HttpStatus;

/**
 * Comment: this is the placeholder for documentation.
 */
public class OrderNotFoundException extends BaseException {

  /**
   * Comment: this is the placeholder for documentation.
   */
  public OrderNotFoundException(String message) {
    super(message, HttpStatus.NOT_FOUND);
  }
}
