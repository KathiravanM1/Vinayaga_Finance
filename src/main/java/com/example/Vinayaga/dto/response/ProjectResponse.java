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
public class ProjectResponse {

    private Long projectId;
    private String projectCode;
    private String projectName;
    private String clientName;
    private String location;
    private Double latitude;
    private Double longitude;
    private BigDecimal contractValue;
    private LocalDate startDate;
    private LocalDate expectedEndDate;
    private String projectStatus;
    private LocalDateTime createdAt;
}
