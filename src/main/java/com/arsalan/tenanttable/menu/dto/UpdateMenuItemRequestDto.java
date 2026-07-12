package com.arsalan.tenanttable.menu.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class UpdateMenuItemRequestDto {

    @Size(max = 100, message = "Menu item name must not exceed 100 characters.")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters.")
    private String description;

    @DecimalMin(value = "0.01", message = "Price must be greater than zero.")
    private BigDecimal price;

    @NotNull(message = "Category is required.")
    private UUID categoryId;

    private Boolean available;
}
