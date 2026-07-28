package com.arsalan.tenanttable.reports.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class ReportFilterRequestDto {
    @NotNull(message = "From date is required.")
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate fromDate;

    @NotNull(message = "To date is required.")
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate toDate;
}
