package com.example.Vinayaga.service;

import com.example.Vinayaga.config.AppConfig;
import com.example.Vinayaga.dto.request.CreateProjectRequest;
import com.example.Vinayaga.dto.response.PagedResponse;
import com.example.Vinayaga.dto.response.ProjectDetailResponse;
import com.example.Vinayaga.dto.response.ProjectLocationResponse;
import com.example.Vinayaga.dto.response.ProjectResponse;
import com.example.Vinayaga.entity.Project;
import com.example.Vinayaga.exception.BusinessValidationException;
import com.example.Vinayaga.exception.DuplicateProjectCodeException;
import com.example.Vinayaga.exception.ResourceNotFoundException;
import com.example.Vinayaga.mapper.ProjectMapper;
import com.example.Vinayaga.repository.ProjectRepository;
import com.example.Vinayaga.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private static final Logger log = LoggerFactory.getLogger(ProjectService.class);

    private final ProjectRepository projectRepository;
    private final TransactionRepository transactionRepository;
    private final ProjectMapper projectMapper;
    private final AppConfig appConfig;

    // -------------------------------------------------------------------------
    // Create
    // -------------------------------------------------------------------------

    @Transactional
    public ProjectResponse createProject(CreateProjectRequest request) {
        log.debug("Creating project with code='{}' for client='{}'",
                request.getProjectCode(), request.getClientName());

        validateProjectDates(request);

        if (projectRepository.existsByProjectCode(request.getProjectCode())) {
            log.warn("Duplicate project code attempted: '{}'", request.getProjectCode());
            throw new DuplicateProjectCodeException(request.getProjectCode());
        }

        Project project = projectMapper.toEntity(request);
        project.setProjectStatus(appConfig.getProject().getDefaultStatus());
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());

        Project saved = projectRepository.save(project);
        log.info("Project created: id={} code='{}' client='{}'",
                saved.getProjectId(), saved.getProjectCode(), saved.getClientName());

        return projectMapper.toResponse(saved);
    }

    // -------------------------------------------------------------------------
    // Read
    // -------------------------------------------------------------------------

    @Transactional(readOnly = true)
    public PagedResponse<ProjectResponse> getAllProjects(String status, int page, int size) {
        log.debug("Fetching projects: status='{}' page={} size={}", status, page, size);

        if (status != null && !status.isBlank()) {
            validateStatus(status);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Project> projectPage = (status != null && !status.isBlank())
                ? projectRepository.findByProjectStatus(status.toUpperCase(), pageable)
                : projectRepository.findAll(pageable);

        log.debug("Found {} projects (total={})", projectPage.getNumberOfElements(),
                projectPage.getTotalElements());
        return buildPagedResponse(projectPage);
    }

    @Transactional(readOnly = true)
    public ProjectDetailResponse getProjectDetails(Long projectId) {
        log.debug("Fetching project details: projectId={}", projectId);

        Project project = findProjectOrThrow(projectId);

        BigDecimal totalIncome  = transactionRepository.sumIncomeByProject(projectId);
        BigDecimal totalExpense = transactionRepository.sumExpenseByProject(projectId);
        BigDecimal balance      = totalIncome.subtract(totalExpense);

        log.debug("Project {} financials: income={} expense={} balance={}",
                projectId, totalIncome, totalExpense, balance);

        return projectMapper.toDetailResponse(project, totalIncome, totalExpense, balance);
    }

    @Transactional(readOnly = true)
    public ProjectLocationResponse getProjectLocation(Long projectId) {
        Project project = findProjectOrThrow(projectId);
        String mapUrl = (project.getLatitude() != null && project.getLongitude() != null)
                ? "https://www.google.com/maps?q=" + project.getLatitude() + "," + project.getLongitude()
                : null;
        return ProjectLocationResponse.builder()
                .projectId(project.getProjectId())
                .projectName(project.getProjectName())
                .location(project.getLocation())
                .latitude(project.getLatitude())
                .longitude(project.getLongitude())
                .shareableMapUrl(mapUrl)
                .build();
    }

    // -------------------------------------------------------------------------
    // Update
    // -------------------------------------------------------------------------

    @Transactional
    public ProjectResponse updateProject(Long projectId, CreateProjectRequest request) {
        log.debug("Updating project: projectId={}", projectId);

        Project project = findProjectOrThrow(projectId);
        validateProjectDates(request);

        if (!project.getProjectCode().equals(request.getProjectCode())
                && projectRepository.existsByProjectCode(request.getProjectCode())) {
            log.warn("Update rejected — project code '{}' already exists", request.getProjectCode());
            throw new DuplicateProjectCodeException(request.getProjectCode());
        }

        project.setProjectCode(request.getProjectCode());
        project.setProjectName(request.getProjectName());
        project.setClientName(request.getClientName());
        project.setLocation(request.getLocation());
        project.setLatitude(request.getLatitude());
        project.setLongitude(request.getLongitude());
        project.setContractValue(request.getContractValue());
        project.setStartDate(request.getStartDate());
        project.setExpectedEndDate(request.getExpectedEndDate());
        project.setNotes(request.getNotes());
        project.setUpdatedAt(LocalDateTime.now());

        Project saved = projectRepository.save(project);
        log.info("Project updated: id={} code='{}'", saved.getProjectId(), saved.getProjectCode());

        return projectMapper.toResponse(saved);
    }

    // -------------------------------------------------------------------------
    // Delete
    // -------------------------------------------------------------------------

    @Transactional
    public void deleteProject(Long projectId) {
        log.debug("Deleting project: projectId={}", projectId);

        Project project = findProjectOrThrow(projectId);

        transactionRepository.deleteByProject_ProjectId(projectId);
        projectRepository.delete(project);

        log.info("Project deleted: id={} code='{}'", projectId, project.getProjectCode());
    }

    // -------------------------------------------------------------------------
    // Status change
    // -------------------------------------------------------------------------

    @Transactional
    public ProjectResponse changeProjectStatus(Long projectId, String newStatus) {
        log.debug("Status change requested: projectId={} newStatus='{}'", projectId, newStatus);

        validateStatus(newStatus);
        Project project = findProjectOrThrow(projectId);

        String previousStatus = project.getProjectStatus();
        validateStatusTransition(previousStatus, newStatus.toUpperCase());

        project.setProjectStatus(newStatus.toUpperCase());
        project.setUpdatedAt(LocalDateTime.now());

        Project saved = projectRepository.save(project);
        log.info("Project status changed: id={} '{}' -> '{}'",
                saved.getProjectId(), previousStatus, saved.getProjectStatus());

        return projectMapper.toResponse(saved);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private Project findProjectOrThrow(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> {
                    log.warn("Project not found: id={}", projectId);
                    return new ResourceNotFoundException("project", projectId);
                });
    }

    private void validateProjectDates(CreateProjectRequest request) {
        if (request.getExpectedEndDate() != null
                && request.getExpectedEndDate().isBefore(request.getStartDate())) {
            throw new BusinessValidationException("Expected end date must be after start date");
        }
    }

    private void validateStatus(String status) {
        List<String> allowed = List.of("ACTIVE", "COMPLETED", "ON_HOLD", "CANCELLED");
        if (!allowed.contains(status.toUpperCase())) {
            throw new BusinessValidationException(
                    "Invalid status '" + status + "'. Must be one of: " + allowed);
        }
    }

    private void validateStatusTransition(String current, String next) {
        if ("CANCELLED".equals(current) && !"CANCELLED".equals(next)) {
            throw new BusinessValidationException(
                    "A cancelled project cannot be moved to status: " + next);
        }
        if ("COMPLETED".equals(current) && "ACTIVE".equals(next)) {
            throw new BusinessValidationException(
                    "A completed project cannot be moved back to ACTIVE");
        }
    }

    private PagedResponse<ProjectResponse> buildPagedResponse(Page<Project> page) {
        return PagedResponse.<ProjectResponse>builder()
                .content(projectMapper.toResponseList(page.getContent()))
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .build();
    }
}
