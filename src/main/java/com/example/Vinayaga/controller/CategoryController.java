package com.example.Vinayaga.controller;

import com.example.Vinayaga.dto.response.ApiResponse;
import com.example.Vinayaga.dto.response.CategoryResponse;
import com.example.Vinayaga.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories(
            @RequestParam(required = false) String type) {
        List<CategoryResponse> response = categoryService.getAllCategories(type);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
