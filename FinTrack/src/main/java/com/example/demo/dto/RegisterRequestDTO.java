package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegisterRequestDTO {
  @NotBlank(message = "Name cannot be empty")
  private String name;
  @NotBlank(message = "Email cannot be empty")
  @Email(message="Email should be valid")
  private String email;
  @NotBlank(message = "Password cannot be empty")
  private String password;
  @NotNull(message = "Salary cannot be empty")
  @Positive(message = "Amount must be greater than zero")
  private BigDecimal salary;
}
