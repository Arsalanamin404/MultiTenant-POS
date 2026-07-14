package com.arsalan.tenanttable.dining_table.dto;

import com.arsalan.tenanttable.dining_table.enums.DiningTableStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Builder
@Data
public class DiningTableResponseDto {
    private UUID id;
    private String tableNumber;
    private int capacity;
    private String description;
    private DiningTableStatus status;
    private UUID createdById;
    private String createdByName;
    private UUID updatedById;
    private String updatedByName;
    private Instant createdAt;
    private Instant updatedAt;
}
