package com.arsalan.tenanttable.category.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Builder
@Data
public class CategoryResponseDto {
    private UUID id;
    private String name;
    private String description;
    private Instant createdAt;
}
