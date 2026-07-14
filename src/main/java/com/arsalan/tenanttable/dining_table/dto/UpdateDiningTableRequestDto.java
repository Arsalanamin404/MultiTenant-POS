package com.arsalan.tenanttable.dining_table.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateDiningTableRequestDto {
    @NotBlank(message = "Table number is required.")
    @Size(max = 20, message = "Table number cannot exceed 20 characters.")
    private String tableNumber;

    @Min(value = 1, message = "Capacity must be at least 1.")
    @Max(value = 50, message = "Capacity cannot exceed 50.")
    private Integer capacity;

    @Size(max = 250, message = "Description cannot exceed 250 characters.")
    private String description;
}
