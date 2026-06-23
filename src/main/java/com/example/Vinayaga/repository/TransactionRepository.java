package com.example.Vinayaga.repository;

import com.example.Vinayaga.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // --- Listing queries ---

    void deleteByProject_ProjectId(Long projectId);

    Page<Transaction> findByProject_ProjectId(Long projectId, Pageable pageable);

    Page<Transaction> findByProject_ProjectIdAndCategory_CategoryType(
            Long projectId, String categoryType, Pageable pageable);

    Page<Transaction> findByProject_ProjectIdAndCategory_CategoryId(
            Long projectId, Long categoryId, Pageable pageable);

    Page<Transaction> findByProject_ProjectIdAndTransactionDateBetween(
            Long projectId, LocalDate fromDate, LocalDate toDate, Pageable pageable);

    Page<Transaction> findByProject_ProjectIdAndCategory_CategoryTypeAndTransactionDateBetween(
            Long projectId, String categoryType, LocalDate fromDate, LocalDate toDate, Pageable pageable);

    // --- Aggregate queries: income / expense per project ---

    @Query("""
            SELECT COALESCE(SUM(t.amount), 0)
            FROM Transaction t
            WHERE t.project.projectId = :projectId
              AND t.category.categoryType = 'INCOME'
            """)
    BigDecimal sumIncomeByProject(@Param("projectId") Long projectId);

    @Query("""
            SELECT COALESCE(SUM(t.amount), 0)
            FROM Transaction t
            WHERE t.project.projectId = :projectId
              AND t.category.categoryType = 'EXPENSE'
            """)
    BigDecimal sumExpenseByProject(@Param("projectId") Long projectId);

    // --- Dashboard: global income / expense across all projects ---

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.category.categoryType = 'INCOME'")
    BigDecimal sumTotalIncome();

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.category.categoryType = 'EXPENSE'")
    BigDecimal sumTotalExpense();

    // --- Dashboard: per-category breakdown ---

    @Query("""
            SELECT t.category.categoryName, COALESCE(SUM(t.amount), 0)
            FROM Transaction t
            WHERE t.category.categoryType = :categoryType
            GROUP BY t.category.categoryName
            ORDER BY SUM(t.amount) DESC
            """)
    List<Object[]> sumAmountGroupedByCategory(@Param("categoryType") String categoryType);

    // --- Date range report ---

    List<Transaction> findByTransactionDateBetweenOrderByTransactionDateDesc(
            LocalDate fromDate, LocalDate toDate);

    @Query("""
            SELECT t FROM Transaction t
            WHERE t.project.projectId = :projectId
              AND t.transactionDate BETWEEN :fromDate AND :toDate
            ORDER BY t.transactionDate DESC
            """)
    List<Transaction> findByProjectAndDateRange(
            @Param("projectId") Long projectId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);

    @Query("""
            SELECT t FROM Transaction t
            WHERE t.transactionDate BETWEEN :fromDate AND :toDate
              AND t.category.categoryType = :categoryType
            ORDER BY t.transactionDate DESC
            """)
    List<Transaction> findByDateRangeAndCategoryType(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("categoryType") String categoryType);

    @Query("""
            SELECT t FROM Transaction t
            WHERE t.project.projectId = :projectId
              AND t.transactionDate BETWEEN :fromDate AND :toDate
              AND t.category.categoryType = :categoryType
            ORDER BY t.transactionDate DESC
            """)
    List<Transaction> findByProjectAndDateRangeAndCategoryType(
            @Param("projectId") Long projectId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("categoryType") String categoryType);

    // --- Report aggregates by date range ---

    @Query("""
            SELECT COALESCE(SUM(t.amount), 0)
            FROM Transaction t
            WHERE t.transactionDate BETWEEN :fromDate AND :toDate
              AND t.category.categoryType = 'INCOME'
            """)
    BigDecimal sumIncomeByDateRange(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);

    @Query("""
            SELECT COALESCE(SUM(t.amount), 0)
            FROM Transaction t
            WHERE t.transactionDate BETWEEN :fromDate AND :toDate
              AND t.category.categoryType = 'EXPENSE'
            """)
    BigDecimal sumExpenseByDateRange(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);
}
