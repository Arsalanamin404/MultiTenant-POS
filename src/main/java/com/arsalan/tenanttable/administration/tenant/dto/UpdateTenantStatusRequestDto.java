package com.arsalan.tenanttable.administration.tenant.dto;

import com.arsalan.tenanttable.tenant.enums.TenantStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UpdateTenantStatusRequestDto(
        @NotNull
        TenantStatus tenantStatus
) {
}
