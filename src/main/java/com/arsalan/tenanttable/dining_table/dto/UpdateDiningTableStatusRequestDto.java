package com.arsalan.tenanttable.dining_table.dto;

import com.arsalan.tenanttable.dining_table.enums.DiningTableStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateDiningTableStatusRequestDto {
    @NotNull(message = "Dining table status is required.")
    DiningTableStatus status;
}
