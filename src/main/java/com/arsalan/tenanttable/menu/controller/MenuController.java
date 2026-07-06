package com.arsalan.tenanttable.menu.controller;

import com.arsalan.tenanttable.common.dto.ApiResponse;
import com.arsalan.tenanttable.menu.dto.CreateMenuItemRequestDto;
import com.arsalan.tenanttable.menu.dto.MenuItemResponseDto;
import com.arsalan.tenanttable.menu.dto.UpdateMenuItemAvailabilityRequestDto;
import com.arsalan.tenanttable.menu.dto.UpdateMenuItemRequestDto;
import com.arsalan.tenanttable.menu.service.IMenuService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/menu-items")
@RequiredArgsConstructor
public class MenuController {
    private final IMenuService menuService;

    @PostMapping
    public ResponseEntity<ApiResponse<MenuItemResponseDto>> createMenuItem(
            @Valid @RequestBody CreateMenuItemRequestDto dto,
            HttpServletRequest request) {

        MenuItemResponseDto menuItem = menuService.createMenuItem(dto);

        ApiResponse<MenuItemResponseDto> response = ApiResponse.success(
                HttpStatus.CREATED.value(),
                "Menu item created successfully.",
                menuItem,
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<MenuItemResponseDto>>> getAll(
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable,
            HttpServletRequest request) {

        Page<MenuItemResponseDto> menuItems = menuService.getAll(pageable);

        ApiResponse<Page<MenuItemResponseDto>> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "Menu item fetched successfully.",
                menuItems,
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MenuItemResponseDto>> getById(
            @PathVariable UUID id,
            HttpServletRequest request) {

        ApiResponse<MenuItemResponseDto> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "Menu item fetched successfully.",
                menuService.getById(id),
                request.getRequestURI()
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MenuItemResponseDto>> updateMenuItem(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateMenuItemRequestDto dto,
            HttpServletRequest request) {

        ApiResponse<MenuItemResponseDto> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "Menu item updated successfully.",
                menuService.updateMenuItem(id, dto),
                request.getRequestURI()
        );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/availability")
    public ResponseEntity<ApiResponse<Void>> updateAvailability(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateMenuItemAvailabilityRequestDto dto,
            HttpServletRequest request) {

        menuService.updateMenuItemAvailability(id, dto.getAvailable());

        ApiResponse<Void> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "Menu item availability updated successfully.",
                null,
                request.getRequestURI()
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMenuItem(
            @PathVariable UUID id,
            HttpServletRequest request) {

        menuService.delete(id);

        ApiResponse<Void> response = ApiResponse.success(
                HttpStatus.NO_CONTENT.value(),
                "Menu item deleted successfully.",
                null,
                request.getRequestURI()
        );

        return ResponseEntity.ok(response);
    }
}
