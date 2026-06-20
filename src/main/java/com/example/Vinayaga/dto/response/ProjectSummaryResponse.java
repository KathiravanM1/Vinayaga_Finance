package com.example.Vinayaga.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectSummaryResponse {

    private Long projectId;
    private String projectCode;
    private String projectName;
    private String clientName;
    private String projectStatus;
    private BigDecimal contractValue;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal currentProfit;
}
