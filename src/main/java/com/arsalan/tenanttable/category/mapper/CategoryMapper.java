package com.arsalan.tenanttable.category.mapper;

import com.arsalan.tenanttable.category.dto.CategoryResponseDto;
import com.arsalan.tenanttable.category.entity.Category;

public final class CategoryMapper {

    private CategoryMapper(){}

    public static CategoryResponseDto toDto(Category category){

        return CategoryResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .createdAt(category.getCreatedAt())
                .build();
    }
}