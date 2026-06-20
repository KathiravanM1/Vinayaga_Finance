package com.example.Vinayaga.mapper;

import com.example.Vinayaga.dto.response.CategoryResponse;
import com.example.Vinayaga.entity.Category;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    // Category → CategoryResponse
    CategoryResponse toResponse(Category category);

    // Category → list of CategoryResponse
    List<CategoryResponse> toResponseList(List<Category> categories);
}
