package com.arsalan.tenanttable.expenses.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateExpenseCategoryRequestDto {

    @Size(max = 100)
    private String name;

    @Size(max = 300)
    private String description;

    private Boolean active;
}
