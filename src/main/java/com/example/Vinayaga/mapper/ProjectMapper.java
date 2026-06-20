package com.example.Vinayaga.mapper;

import com.example.Vinayaga.dto.request.CreateProjectRequest;
import com.example.Vinayaga.dto.response.ProjectDetailResponse;
import com.example.Vinayaga.dto.response.ProjectResponse;
import com.example.Vinayaga.entity.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    // CreateProjectRequest → Project
    // Ignore all fields the backend generates — ID, timestamps, status
    @Mapping(target = "projectId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Project toEntity(CreateProjectRequest request);

    // Project → ProjectResponse (list item — no notes, no financial summaries)
    ProjectResponse toResponse(Project project);

    // Project → list of ProjectResponse
    List<ProjectResponse> toResponseList(List<Project> projects);

    // Project → ProjectDetailResponse
    // totalIncome, totalExpense, balance are computed by the service and passed in separately
    @Mapping(target = "totalIncome", expression = "java(totalIncome)")
    @Mapping(target = "totalExpense", expression = "java(totalExpense)")
    @Mapping(target = "balance", expression = "java(balance)")
    ProjectDetailResponse toDetailResponse(
            Project project,
            BigDecimal totalIncome,
            BigDecimal totalExpense,
            BigDecimal balance
    );
}
