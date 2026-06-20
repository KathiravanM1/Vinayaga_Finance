package com.example.Vinayaga.service;

import com.example.Vinayaga.config.AppConfig;
import com.example.Vinayaga.dto.request.CreateTransactionRequest;
import com.example.Vinayaga.dto.response.PagedResponse;
import com.example.Vinayaga.dto.response.ReportResponse;
import com.example.Vinayaga.dto.response.TransactionResponse;
import com.example.Vinayaga.entity.Category;
import com.example.Vinayaga.entity.Project;
import com.example.Vinayaga.entity.Transaction;
import com.example.Vinayaga.exception.BusinessValidationException;
import com.example.Vinayaga.exception.ResourceNotFoundException;
import com.example.Vinayaga.mapper.TransactionMapper;
import com.example.Vinayaga.repository.CategoryRepository;
import com.example.Vinayaga.repository.ProjectRepository;
import com.example.Vinayaga.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;
    private final ProjectRepository projectRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionMapper transactionMapper;
    private final AppConfig appConfig;

    // -------------------------------------------------------------------------
    // Create
    // -------------------------------------------------------------------------

    @Transactional
    public TransactionResponse createTransaction(Long projectId, CreateTransactionRequest request) {
        log.debug("Creating transaction: projectId={} categoryId={} amount={}",
                projectId, request.getCategoryId(), request.getAmount());

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> {
                    log.warn("Transaction creation failed — project not found: id={}", projectId);
                    return new ResourceNotFoundException("project", projectId);
                });

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> {
                    log.warn("Transaction creation failed — category not found: id={}",
                            request.getCategoryId());
                    return new ResourceNotFoundException("category", request.getCategoryId());
                });

        Transaction transaction = transactionMapper.toEntity(request);
        transaction.setProject(project);
        transaction.setCategory(category);
        transaction.setCreatedAt(LocalDateTime.now());

        Transaction saved = transactionRepository.save(transaction);
        log.info("Transaction created: id={} projectId={} category='{}' type='{}' amount={}",
                saved.getTransactionId(), projectId,
                category.getCategoryName(), category.getCategoryType(), saved.getAmount());

        return transactionMapper.toResponse(saved);
    }

    // -------------------------------------------------------------------------
    // Read — transactions by project with optional filters
    // -------------------------------------------------------------------------

    @Transactional(readOnly = true)
    public PagedResponse<TransactionResponse> getTransactionsByProject(
            Long projectId, String categoryType, Long categoryId,
            LocalDate fromDate, LocalDate toDate, int page, int size) {

        log.debug("Fetching transactions: projectId={} categoryType='{}' categoryId={} from={} to={} page={} size={}",
                projectId, categoryType, categoryId, fromDate, toDate, page, size);

        if (!projectRepository.existsById(projectId)) {
            log.warn("Transactions requested for non-existent project: id={}", projectId);
            throw new ResourceNotFoundException("project", projectId);
        }
        validateDateRange(fromDate, toDate);

        Pageable pageable = PageRequest.of(page, size, Sort.by("transactionDate").descending());
        Page<Transaction> result = resolveTransactionPage(
                projectId, categoryType, categoryId, fromDate, toDate, pageable);

        log.debug("Found {} transactions for projectId={}", result.getTotalElements(), projectId);

        return PagedResponse.<TransactionResponse>builder()
                .content(transactionMapper.toResponseList(result.getContent()))
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .pageNumber(result.getNumber())
                .pageSize(result.getSize())
                .build();
    }

    // -------------------------------------------------------------------------
    // Read — date range report
    // -------------------------------------------------------------------------

    @Transactional(readOnly = true)
    public ReportResponse getTransactionsByDateRange(
            LocalDate fromDate, LocalDate toDate, Long projectId, String categoryType) {

        log.debug("Generating report: from={} to={} projectId={} type='{}'",
                fromDate, toDate, projectId, categoryType);

        if (fromDate == null || toDate == null) {
            throw new BusinessValidationException("fromDate and toDate are required");
        }
        validateDateRange(fromDate, toDate);

        long days = ChronoUnit.DAYS.between(fromDate, toDate);
        int maxDays = appConfig.getReport().getMaxDays();
        if (days > maxDays) {
            throw new BusinessValidationException(
                    "Date range must not exceed " + maxDays + " days");
        }
        if (projectId != null && !projectRepository.existsById(projectId)) {
            log.warn("Report requested for non-existent project: id={}", projectId);
            throw new ResourceNotFoundException("project", projectId);
        }

        List<Transaction> transactions = resolveReportTransactions(
                fromDate, toDate, projectId, categoryType);

        BigDecimal totalIncome  = sumByType(transactions, "INCOME");
        BigDecimal totalExpense = sumByType(transactions, "EXPENSE");
        BigDecimal netBalance   = totalIncome.subtract(totalExpense);

        log.info("Report generated: from={} to={} projectId={} transactions={} income={} expense={} net={}",
                fromDate, toDate, projectId, transactions.size(),
                totalIncome, totalExpense, netBalance);

        return ReportResponse.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .netBalance(netBalance)
                .transactions(transactionMapper.toReportResponseList(transactions))
                .build();
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private Page<Transaction> resolveTransactionPage(
            Long projectId, String categoryType, Long categoryId,
            LocalDate fromDate, LocalDate toDate, Pageable pageable) {

        boolean hasCategoryType = categoryType != null && !categoryType.isBlank();
        boolean hasDateRange    = fromDate != null && toDate != null;

        if (hasCategoryType && hasDateRange) {
            return transactionRepository
                    .findByProject_ProjectIdAndCategory_CategoryTypeAndTransactionDateBetween(
                            projectId, categoryType.toUpperCase(), fromDate, toDate, pageable);
        }
        if (hasCategoryType) {
            return transactionRepository
                    .findByProject_ProjectIdAndCategory_CategoryType(
                            projectId, categoryType.toUpperCase(), pageable);
        }
        if (categoryId != null) {
            return transactionRepository
                    .findByProject_ProjectIdAndCategory_CategoryId(projectId, categoryId, pageable);
        }
        if (hasDateRange) {
            return transactionRepository
                    .findByProject_ProjectIdAndTransactionDateBetween(
                            projectId, fromDate, toDate, pageable);
        }
        return transactionRepository.findByProject_ProjectId(projectId, pageable);
    }

    private List<Transaction> resolveReportTransactions(
            LocalDate fromDate, LocalDate toDate, Long projectId, String categoryType) {

        boolean hasProject      = projectId != null;
        boolean hasCategoryType = categoryType != null && !categoryType.isBlank();

        if (hasProject && hasCategoryType) {
            return transactionRepository.findByProjectAndDateRangeAndCategoryType(
                    projectId, fromDate, toDate, categoryType.toUpperCase());
        }
        if (hasProject) {
            return transactionRepository.findByProjectAndDateRange(projectId, fromDate, toDate);
        }
        if (hasCategoryType) {
            return transactionRepository.findByDateRangeAndCategoryType(
                    fromDate, toDate, categoryType.toUpperCase());
        }
        return transactionRepository
                .findByTransactionDateBetweenOrderByTransactionDateDesc(fromDate, toDate);
    }

    private void validateDateRange(LocalDate fromDate, LocalDate toDate) {
        if (fromDate != null && toDate != null && toDate.isBefore(fromDate)) {
            throw new BusinessValidationException("toDate must not be before fromDate");
        }
    }

    private BigDecimal sumByType(List<Transaction> transactions, String type) {
        return transactions.stream()
                .filter(t -> type.equals(t.getCategory().getCategoryType()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
