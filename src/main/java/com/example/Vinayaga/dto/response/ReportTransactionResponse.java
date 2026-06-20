package com.example.Vinayaga.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportTransactionResponse {

    private Long transactionId;
    private Long projectId;
    private String projectName;
    private String categoryName;
    private String categoryType;
    private BigDecimal amount;
    private String paymentMode;
    private LocalDate transactionDate;
    private String description;
}
