package com.arsalan.tenanttable.menu.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMenuItemAvailabilityRequestDto {

    @NotNull(message = "Availability is required.")
    private Boolean available;
}