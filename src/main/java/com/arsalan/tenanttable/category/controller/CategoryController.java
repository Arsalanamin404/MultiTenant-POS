package com.arsalan.tenanttable.category.controller;

import com.arsalan.tenanttable.category.dto.CategoryResponseDto;
import com.arsalan.tenanttable.category.dto.CreateCategoryRequestDto;
import com.arsalan.tenanttable.category.dto.UpdateCategoryRequestDto;
import com.arsalan.tenanttable.category.service.ICategoryService;
import com.arsalan.tenanttable.common.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final ICategoryService categoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponseDto>> create(
            @Valid @RequestBody CreateCategoryRequestDto dto,
            HttpServletRequest request
    ) {

        CategoryResponseDto category = categoryService.create(dto);

        ApiResponse<CategoryResponseDto> response =
                ApiResponse.success(
                        HttpStatus.CREATED.value(),
                        "Category created successfully.",
                        category,
                        request.getRequestURI()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<CategoryResponseDto>>> getAll(
            Pageable pageable,
            HttpServletRequest request
    ) {

        Page<CategoryResponseDto> categories = categoryService.getAll(pageable);

        ApiResponse<Page<CategoryResponseDto>> response =
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Categories fetched successfully.",
                        categories,
                        request.getRequestURI()
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponseDto>> getById(
            @PathVariable UUID id,
            HttpServletRequest request
    ) {

        CategoryResponseDto category = categoryService.getById(id);

        ApiResponse<CategoryResponseDto> response =
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Category fetched successfully.",
                        category,
                        request.getRequestURI()
                );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponseDto>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCategoryRequestDto dto,
            HttpServletRequest request
    ) {

        CategoryResponseDto category = categoryService.update(id, dto);

        ApiResponse<CategoryResponseDto> response =
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Category updated successfully.",
                        category,
                        request.getRequestURI()
                );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable UUID id,
            HttpServletRequest request
    ) {

        categoryService.delete(id);

        ApiResponse<Void> response =
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Category deleted successfully.",
                        null,
                        request.getRequestURI()
                );

        return ResponseEntity.ok(response);
    }
}
