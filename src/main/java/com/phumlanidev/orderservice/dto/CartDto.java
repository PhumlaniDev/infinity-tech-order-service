package com.phumlanidev.orderservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Comment: this is the placeholder for documentation.
 */

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class CartDto {

  @NotBlank(message = "User ID is required")
  private String userId; // foreign key reference
  @NotNull(message = "Total price is required")
  private BigDecimal totalPrice;
  @JsonProperty("items")
  @JsonSetter(nulls = Nulls.AS_EMPTY)
  @NotEmpty(message = "Cart items cannot be empty")
  private List<CartItemDto> cartItems = new ArrayList<>();
}
