package com.arsalan.tenanttable.menu.mapper;

import com.arsalan.tenanttable.menu.dto.MenuItemResponseDto;
import com.arsalan.tenanttable.menu.entity.MenuItem;

public final class MenuItemMapper {
    private MenuItemMapper(){}

    public static MenuItemResponseDto toDto(MenuItem menuItem){
        return MenuItemResponseDto.builder()
                .id(menuItem.getId())
                .name(menuItem.getName())
                .description(menuItem.getDescription())
                .price(menuItem.getPrice())
                .available(menuItem.isAvailable())
                .categoryId(menuItem.getCategory().getId())
                .categoryName(menuItem.getCategory().getName())
                .createdById(menuItem.getCreatedBy().getId())
                .createdByName(menuItem.getCreatedBy().getFullName())
                .updatedById(menuItem.getUpdatedBy().getId())
                .updatedByName(menuItem.getUpdatedBy().getFullName())
                .createdAt(menuItem.getCreatedAt())
                .updatedAt(menuItem.getUpdatedAt())
                .build();
    }
}
