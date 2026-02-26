package com.example.demo.dto;
import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinancialNarrativeResponseDTO {
	    private BigDecimal totalSpending;
	    private String highestSpendingCategory;
	    private String lowestSpendingCategory;

	    private List<CategoryBreakdownDTO> categoryBreakdown;

	    private String narrative;

}
