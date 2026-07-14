package com.arsalan.tenanttable.dining_table.dto;

import com.arsalan.tenanttable.dining_table.enums.DiningTableStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateDiningTableStatusRequestDto {
    @NotNull(message = "Dining table status is required.")
    DiningTableStatus status;
}
