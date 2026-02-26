package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ExpenseResponseDTO {
	private Long id;
	private BigDecimal amt;
	private CategoryResponseDTO category;
	private LocalDate date;
	

}
