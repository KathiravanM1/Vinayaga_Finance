package com.example.Vinayaga.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {

    private Long transactionId;
    private Long projectId;
    private String projectName;
    private Long categoryId;
    private String categoryName;
    private String categoryType;
    private BigDecimal amount;
    private String description;
    private String paymentMode;
    private LocalDate transactionDate;
    private String remarks;
    private String attachmentUrl;
    private LocalDateTime createdAt;
}
