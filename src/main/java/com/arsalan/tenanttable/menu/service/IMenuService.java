package com.arsalan.tenanttable.menu.service;

import com.arsalan.tenanttable.menu.dto.CreateMenuItemRequestDto;
import com.arsalan.tenanttable.menu.dto.MenuItemResponseDto;
import com.arsalan.tenanttable.menu.dto.UpdateMenuItemRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IMenuService {
    MenuItemResponseDto createMenuItem(CreateMenuItemRequestDto dto);

    Page<MenuItemResponseDto> getAll(Pageable pageable);

    MenuItemResponseDto getById(UUID id);

    MenuItemResponseDto updateMenuItem(UUID id, UpdateMenuItemRequestDto dto);

    void updateMenuItemAvailability(UUID id, boolean available);

    void delete(UUID id);
}
