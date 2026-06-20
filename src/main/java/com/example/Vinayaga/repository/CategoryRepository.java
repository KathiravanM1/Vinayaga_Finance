package com.example.Vinayaga.repository;

import com.example.Vinayaga.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByCategoryType(String categoryType);

    boolean existsByCategoryName(String categoryName);
}
