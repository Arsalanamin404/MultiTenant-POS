package com.arsalan.tenanttable.staff.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStaffStatusRequestDto {
    @NotNull(message = "Staff status is required [true/false].")
    private Boolean active;
}
