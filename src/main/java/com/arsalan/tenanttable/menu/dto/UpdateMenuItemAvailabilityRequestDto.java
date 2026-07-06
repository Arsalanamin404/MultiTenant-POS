package com.arsalan.tenanttable.menu.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateMenuItemAvailabilityRequestDto {

    @NotNull(message = "Availability is required.")
    private Boolean available;
}