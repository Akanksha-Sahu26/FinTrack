package com.example.demo.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RiskDetectionResponseDTO {
	private List<String> riskyCategories;

    private List<String> safeCategories;

    private String riskAnalysisNarrative;

}
