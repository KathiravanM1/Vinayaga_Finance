package com.example.Vinayaga.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    private long totalProjects;
    private long activeProjects;
    private long completedProjects;
    private long onHoldProjects;
    private long cancelledProjects;
    private BigDecimal totalContractValue;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal netBalance;
    private List<CategoryBreakdownResponse> incomeBreakdown;
    private List<CategoryBreakdownResponse> expenseBreakdown;
}
