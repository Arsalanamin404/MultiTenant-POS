package com.arsalan.tenanttable.category.service;

import com.arsalan.tenanttable.category.dto.CategoryResponseDto;
import com.arsalan.tenanttable.category.dto.CreateCategoryRequestDto;
import com.arsalan.tenanttable.category.dto.UpdateCategoryRequestDto;
import com.arsalan.tenanttable.common.dto.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ICategoryService {
    CategoryResponseDto create(CreateCategoryRequestDto dto);

    Page<CategoryResponseDto> getAll(Pageable pageable);

    CategoryResponseDto getById(UUID id);

    CategoryResponseDto update(UUID id, UpdateCategoryRequestDto dto);

    void delete(UUID id);
}
