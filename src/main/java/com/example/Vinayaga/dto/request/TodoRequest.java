package com.example.Vinayaga.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor
public class TodoRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    private String description;

    @Pattern(regexp = "PENDING|IN_PROGRESS|DONE", message = "Status must be one of: PENDING, IN_PROGRESS, DONE")
    private String status;

    @Pattern(regexp = "LOW|MEDIUM|HIGH", message = "Priority must be one of: LOW, MEDIUM, HIGH")
    private String priority;

    private LocalDate dueDate;
}
