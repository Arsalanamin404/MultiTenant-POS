package com.arsalan.tenanttable.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCategoryRequestDto {
    @NotBlank(message = "Category name is required")
    @Size(max = 35, message = "Category name cannot exceed 35 characters")
    private String name;

    @Size(max = 255, message = "Category description cannot exceed 255 characters")
    private String description;
}
