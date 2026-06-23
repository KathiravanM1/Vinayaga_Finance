package com.example.Vinayaga.controller;

import com.example.Vinayaga.dto.request.CreateProjectRequest;
import com.example.Vinayaga.dto.response.ApiResponse;
import com.example.Vinayaga.dto.response.PagedResponse;
import com.example.Vinayaga.dto.response.ProjectDetailResponse;
import com.example.Vinayaga.dto.response.ProjectLocationResponse;
import com.example.Vinayaga.dto.response.ProjectResponse;
import com.example.Vinayaga.service.ProjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Validated
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProjectResponse>> createProject(
            @Valid @RequestBody CreateProjectRequest request) {
        ProjectResponse response = projectService.createProject(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Project created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<ProjectResponse>>> getAllProjects(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        PagedResponse<ProjectResponse> response = projectService.getAllProjects(status, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ProjectDetailResponse>> getProjectDetails(
            @PathVariable Long projectId) {
        ProjectDetailResponse response = projectService.getProjectDetails(projectId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{projectId}/location")
    public ResponseEntity<ApiResponse<ProjectLocationResponse>> getProjectLocation(
            @PathVariable Long projectId) {
        return ResponseEntity.ok(ApiResponse.success(projectService.getProjectLocation(projectId)));
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ProjectResponse>> updateProject(
            @PathVariable Long projectId,
            @Valid @RequestBody CreateProjectRequest request) {
        ProjectResponse response = projectService.updateProject(projectId, request);
        return ResponseEntity.ok(ApiResponse.success("Project updated successfully", response));
    }

    @PatchMapping("/{projectId}/status")
    public ResponseEntity<ApiResponse<ProjectResponse>> changeProjectStatus(
            @PathVariable Long projectId,
            @RequestParam String status) {
        ProjectResponse response = projectService.changeProjectStatus(projectId, status);
        return ResponseEntity.ok(ApiResponse.success("Project status updated successfully", response));
    }
}
