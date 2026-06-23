package com.example.Vinayaga.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class CreateProjectRequest {

    @NotBlank(message = "Project code is required")
    @Size(max = 50, message = "Project code must not exceed 50 characters")
    private String projectCode;

    @NotBlank(message = "Project name is required")
    @Size(max = 255, message = "Project name must not exceed 255 characters")
    private String projectName;

    @NotBlank(message = "Client name is required")
    @Size(max = 255, message = "Client name must not exceed 255 characters")
    private String clientName;

    @Size(max = 255, message = "Location must not exceed 255 characters")
    private String location;

    @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
    @DecimalMax(value = "90.0",  message = "Latitude must be <= 90")
    private Double latitude;

    @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
    @DecimalMax(value = "180.0",  message = "Longitude must be <= 180")
    private Double longitude;

    @DecimalMin(value = "0.01", message = "Contract value must be greater than 0")
    @Digits(integer = 13, fraction = 2, message = "Contract value must have at most 13 integer digits and 2 decimal places")
    private BigDecimal contractValue;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    private LocalDate expectedEndDate;

    @NotBlank(message = "Project status is required")
    @Pattern(
        regexp = "ACTIVE|COMPLETED|ON_HOLD|CANCELLED",
        message = "Project status must be one of: ACTIVE, COMPLETED, ON_HOLD, CANCELLED"
    )
    private String projectStatus;

    private String notes;
}
