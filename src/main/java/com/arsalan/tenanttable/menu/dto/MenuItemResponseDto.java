package com.arsalan.tenanttable.menu.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Builder
@Data
public class MenuItemResponseDto {
    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private boolean available;
    private UUID categoryId;
    private String categoryName;
    private UUID createdById;
    private String createdByName;
    private UUID updatedById;
    private String updatedByName;
    private Instant createdAt;
    private Instant updatedAt;
}
