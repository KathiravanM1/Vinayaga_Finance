package com.example.Vinayaga.repository;

import com.example.Vinayaga.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    Page<Todo> findByStatus(String status, Pageable pageable);
}
