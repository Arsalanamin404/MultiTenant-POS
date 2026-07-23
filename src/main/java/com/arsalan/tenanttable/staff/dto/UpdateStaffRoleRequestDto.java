package com.arsalan.tenanttable.staff.dto;

import com.arsalan.tenanttable.common.enums.TenantRole;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateStaffRoleRequestDto {
    @NotNull(message = "Tenant role is required.")
    private TenantRole tenantRole;
}
