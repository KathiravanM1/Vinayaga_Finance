package com.example.Vinayaga.controller;

import com.example.Vinayaga.dto.request.CreateTransactionRequest;
import com.example.Vinayaga.dto.response.ApiResponse;
import com.example.Vinayaga.dto.response.PagedResponse;
import com.example.Vinayaga.dto.response.ReportResponse;
import com.example.Vinayaga.dto.response.TransactionResponse;
import com.example.Vinayaga.service.TransactionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Validated
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/projects/{projectId}/transactions")
    public ResponseEntity<ApiResponse<TransactionResponse>> createTransaction(
            @PathVariable Long projectId,
            @Valid @RequestBody CreateTransactionRequest request) {
        TransactionResponse response = transactionService.createTransaction(projectId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Transaction created successfully", response));
    }

    @GetMapping("/projects/{projectId}/transactions")
    public ResponseEntity<ApiResponse<PagedResponse<TransactionResponse>>> getTransactionsByProject(
            @PathVariable Long projectId,
            @RequestParam(required = false) String categoryType,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        PagedResponse<TransactionResponse> response = transactionService.getTransactionsByProject(
                projectId, categoryType, categoryId, fromDate, toDate, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * preset values: TODAY | THIS_WEEK | THIS_MONTH
     * When preset is supplied, fromDate/toDate are ignored.
     * When preset is absent, fromDate and toDate must be provided.
     */
    @GetMapping("/reports")
    public ResponseEntity<ApiResponse<ReportResponse>> getReport(
            @RequestParam(required = false) String preset,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) String type) {

        LocalDate[] range = resolveDateRange(preset, fromDate, toDate);
        ReportResponse response = transactionService.getTransactionsByDateRange(
                range[0], range[1], projectId, type);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    private LocalDate[] resolveDateRange(String preset, LocalDate fromDate, LocalDate toDate) {
        if (preset != null) {
            LocalDate today = LocalDate.now();
            return switch (preset.toUpperCase()) {
                case "TODAY"      -> new LocalDate[]{today, today};
                case "THIS_WEEK"  -> new LocalDate[]{
                        today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)),
                        today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))};
                case "THIS_MONTH" -> new LocalDate[]{
                        today.with(TemporalAdjusters.firstDayOfMonth()),
                        today.with(TemporalAdjusters.lastDayOfMonth())};
                default -> throw new com.example.Vinayaga.exception.BusinessValidationException(
                        "Invalid preset '" + preset + "'. Use: TODAY, THIS_WEEK, THIS_MONTH");
            };
        }
        return new LocalDate[]{fromDate, toDate};
    }
}
