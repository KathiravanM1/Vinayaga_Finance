package com.example.Vinayaga.controller;

import com.example.Vinayaga.dto.response.ApiResponse;
import com.example.Vinayaga.dto.response.DashboardResponse;
import com.example.Vinayaga.dto.response.ProjectSummaryResponse;
import com.example.Vinayaga.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getDashboard()));
    }

    @GetMapping("/projects/{projectId}/summary")
    public ResponseEntity<ApiResponse<ProjectSummaryResponse>> getProjectSummary(
            @PathVariable Long projectId) {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getProjectSummary(projectId)));
    }
}
