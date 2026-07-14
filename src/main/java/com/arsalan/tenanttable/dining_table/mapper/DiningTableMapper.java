package com.arsalan.tenanttable.dining_table.mapper;

import com.arsalan.tenanttable.dining_table.dto.DiningTableResponseDto;
import com.arsalan.tenanttable.dining_table.entity.DiningTable;

public final class DiningTableMapper {
    private DiningTableMapper () {}

    public static DiningTableResponseDto toDto(DiningTable diningTable){
        return DiningTableResponseDto.builder()
                .id(diningTable.getId())
                .tableNumber(diningTable.getTableNumber())
                .capacity(diningTable.getCapacity())
                .description(diningTable.getDescription())
                .status(diningTable.getStatus())
                .createdById(diningTable.getCreatedBy().getId())
                .createdByName(diningTable.getCreatedBy().getFullName())
                .updatedById(diningTable.getUpdatedBy().getId())
                .updatedByName(diningTable.getUpdatedBy().getFullName())
                .createdAt(diningTable.getCreatedAt())
                .updatedAt(diningTable.getUpdatedAt())
                .build();
    }

}
