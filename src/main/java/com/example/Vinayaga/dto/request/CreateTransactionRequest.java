package com.example.Vinayaga.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class CreateTransactionRequest {

    @NotNull(message = "Category ID is required")
    @Positive(message = "Category ID must be a positive number")
    private Long categoryId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 13, fraction = 2, message = "Amount must have at most 13 integer digits and 2 decimal places")
    private BigDecimal amount;

    private String description;

    @Pattern(
        regexp = "CASH|UPI|BANK_TRANSFER|CHEQUE",
        message = "Payment mode must be one of: CASH, UPI, BANK_TRANSFER, CHEQUE"
    )
    private String paymentMode;

    @NotNull(message = "Transaction date is required")
    @PastOrPresent(message = "Transaction date cannot be a future date")
    private LocalDate transactionDate;

    private String remarks;

    @URL(message = "Attachment URL must be a valid URL")
    @Size(max = 500, message = "Attachment URL must not exceed 500 characters")
    private String attachmentUrl;
}
