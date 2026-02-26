package com.example.demo.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinancialHealthCareResponseDTO {
	 private Integer healthScore;   // 0-100

	    private String healthLevel;    // LOW, MODERATE, GOOD, EXCELLENT

	    private String explanation;
}
