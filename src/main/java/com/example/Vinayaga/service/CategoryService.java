package com.example.Vinayaga.service;

import com.example.Vinayaga.dto.request.CreateProjectRequest;
import com.example.Vinayaga.dto.response.CategoryResponse;
import com.example.Vinayaga.entity.Category;
import com.example.Vinayaga.exception.BusinessValidationException;
import com.example.Vinayaga.mapper.CategoryMapper;
import com.example.Vinayaga.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories(String type) {
        List<Category> categories = (type != null && !type.isBlank())
                ? categoryRepository.findByCategoryType(type.toUpperCase())
                : categoryRepository.findAll();
        return categoryMapper.toResponseList(categories);
    }

    @Transactional
    public CategoryResponse createCategory(String categoryName, String categoryType) {
        if (!categoryType.equals("INCOME") && !categoryType.equals("EXPENSE")) {
            throw new BusinessValidationException(
                    "Category type must be INCOME or EXPENSE");
        }
        if (categoryRepository.existsByCategoryName(categoryName.toUpperCase())) {
            throw new BusinessValidationException(
                    "Category '" + categoryName + "' already exists");
        }
        Category category = Category.builder()
                .categoryName(categoryName.toUpperCase())
                .categoryType(categoryType.toUpperCase())
                .createdAt(LocalDateTime.now())
                .build();
        return categoryMapper.toResponse(categoryRepository.save(category));
    }
}
