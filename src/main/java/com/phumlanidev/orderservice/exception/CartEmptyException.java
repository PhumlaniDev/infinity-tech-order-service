package com.phumlanidev.orderservice.exception;


import org.springframework.http.HttpStatus;


/**
 * Comment: this is the placeholder for documentation.
 */
public class CartEmptyException extends BaseException {

  /**
   * Comment: this is the placeholder for documentation.
   */
  public CartEmptyException(String message) {
    super(message, HttpStatus.BAD_REQUEST);
  }
}
