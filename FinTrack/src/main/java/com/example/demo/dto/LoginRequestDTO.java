package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginRequestDTO {
	@NotBlank(message = "Email cannot be empty")
	  @Email(message="Email should be valid")
	  private String email;
	  @NotBlank(message = "Password cannot be empty")
	  private String password;
}
