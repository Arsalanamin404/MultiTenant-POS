package com.arsalan.tenanttable.staff.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateStaffStatusRequestDto {
    @NotNull(message = "Staff status is required [true/false].")
    private Boolean active;
}
