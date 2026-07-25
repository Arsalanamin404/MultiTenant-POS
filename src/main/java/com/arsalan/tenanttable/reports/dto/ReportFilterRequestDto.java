package com.arsalan.tenanttable.reports.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReportFilterRequestDto {
    @NotNull(message = "From date is required.")
    private LocalDate fromDate;

    @NotNull(message = "To date is required.")
    private LocalDate toDate;
}
