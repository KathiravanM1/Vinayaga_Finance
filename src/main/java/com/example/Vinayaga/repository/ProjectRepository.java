package com.example.Vinayaga.repository;

import com.example.Vinayaga.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    boolean existsByProjectCode(String projectCode);

    Optional<Project> findByProjectCode(String projectCode);

    Page<Project> findByProjectStatus(String projectStatus, Pageable pageable);

    // Used for project code generation — finds the highest numeric suffix
    @Query("SELECT p.projectCode FROM Project p ORDER BY p.projectId DESC LIMIT 1")
    Optional<String> findLatestProjectCode();

    // Dashboard counts
    long countByProjectStatus(String projectStatus);

    // Dashboard total contract value
    @Query("SELECT COALESCE(SUM(p.contractValue), 0) FROM Project p")
    java.math.BigDecimal sumTotalContractValue();
}
