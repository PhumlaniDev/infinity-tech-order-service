package com.phumlanidev.orderservice.dto;

import com.phumlanidev.orderservice.enums.RoleMapping;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

  @NotBlank(message = "First name is required")
  private String firstName;
  @NotBlank(message = "Last name is required")
  private String lastName;
  @NotBlank(message = "Username is required")
  private String username;
  @NotBlank(message = "Email is required")
  @Email(message = "Email should be valid")
  private String email;
  @NotBlank(message = "Password is required")
  private String password;
  @NotBlank(message = "Phone number is required")
  private String phoneNumber;
  @NotNull(message = "Role is required")
  private RoleMapping role;
}
