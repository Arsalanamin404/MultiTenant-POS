package com.arsalan.tenanttable.administration.tenant.dto;

import com.arsalan.tenanttable.tenant.enums.PlanType;
import jakarta.validation.constraints.NotNull;

public record UpdateTenantPlanRequestDto(

        @NotNull(message = "Plan type is required.")
        PlanType planType

) {
}
