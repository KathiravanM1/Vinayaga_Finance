package com.example.Vinayaga.service;

import com.example.Vinayaga.dto.response.CategoryBreakdownResponse;
import com.example.Vinayaga.dto.response.DashboardResponse;
import com.example.Vinayaga.dto.response.ProjectSummaryResponse;
import com.example.Vinayaga.entity.Project;
import com.example.Vinayaga.exception.ResourceNotFoundException;
import com.example.Vinayaga.repository.ProjectRepository;
import com.example.Vinayaga.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final Logger log = LoggerFactory.getLogger(DashboardService.class);

    private final ProjectRepository projectRepository;
    private final TransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard() {
        log.debug("Building global dashboard");

        long totalProjects     = projectRepository.count();
        long activeProjects    = projectRepository.countByProjectStatus("ACTIVE");
        long completedProjects = projectRepository.countByProjectStatus("COMPLETED");
        long onHoldProjects    = projectRepository.countByProjectStatus("ON_HOLD");
        long cancelledProjects = projectRepository.countByProjectStatus("CANCELLED");

        BigDecimal totalContractValue = projectRepository.sumTotalContractValue();
        BigDecimal totalIncome        = transactionRepository.sumTotalIncome();
        BigDecimal totalExpense       = transactionRepository.sumTotalExpense();
        BigDecimal netBalance         = totalIncome.subtract(totalExpense);

        List<CategoryBreakdownResponse> incomeBreakdown =
                buildBreakdown(transactionRepository.sumAmountGroupedByCategory("INCOME"));
        List<CategoryBreakdownResponse> expenseBreakdown =
                buildBreakdown(transactionRepository.sumAmountGroupedByCategory("EXPENSE"));

        log.info("Dashboard: totalProjects={} active={} income={} expense={} net={}",
                totalProjects, activeProjects, totalIncome, totalExpense, netBalance);

        return DashboardResponse.builder()
                .totalProjects(totalProjects)
                .activeProjects(activeProjects)
                .completedProjects(completedProjects)
                .onHoldProjects(onHoldProjects)
                .cancelledProjects(cancelledProjects)
                .totalContractValue(totalContractValue)
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .netBalance(netBalance)
                .incomeBreakdown(incomeBreakdown)
                .expenseBreakdown(expenseBreakdown)
                .build();
    }

    @Transactional(readOnly = true)
    public ProjectSummaryResponse getProjectSummary(Long projectId) {
        log.debug("Building project summary: projectId={}", projectId);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> {
                    log.warn("Project summary requested for non-existent project: id={}", projectId);
                    return new ResourceNotFoundException("project", projectId);
                });

        BigDecimal totalIncome   = transactionRepository.sumIncomeByProject(projectId);
        BigDecimal totalExpense  = transactionRepository.sumExpenseByProject(projectId);
        BigDecimal currentProfit = totalIncome.subtract(totalExpense);

        log.info("Project summary: id={} code='{}' income={} expense={} profit={}",
                projectId, project.getProjectCode(), totalIncome, totalExpense, currentProfit);

        return ProjectSummaryResponse.builder()
                .projectId(project.getProjectId())
                .projectCode(project.getProjectCode())
                .projectName(project.getProjectName())
                .clientName(project.getClientName())
                .projectStatus(project.getProjectStatus())
                .contractValue(project.getContractValue())
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .currentProfit(currentProfit)
                .build();
    }

    private List<CategoryBreakdownResponse> buildBreakdown(List<Object[]> rows) {
        return rows.stream()
                .map(row -> CategoryBreakdownResponse.builder()
                        .categoryName((String) row[0])
                        .totalAmount((BigDecimal) row[1])
                        .build())
                .toList();
    }
}
