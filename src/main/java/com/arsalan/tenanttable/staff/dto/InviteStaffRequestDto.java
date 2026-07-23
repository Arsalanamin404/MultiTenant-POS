package com.arsalan.tenanttable.staff.dto;

import com.arsalan.tenanttable.common.enums.TenantRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class InviteStaffRequestDto {

    @NotBlank(message = "Full name is required.")
    private String fullName;

    @NotBlank(message = "Email is required.")
    @Email(message = "Invalid email address.")
    private String email;

    @NotBlank(message = "Phone number is required.")
    @Pattern(
            regexp = "^[6-9]\\d{9}$",
            message = "Please enter a valid 10-digit Indian mobile number."
    )
    private String phoneNumber;

    @NotNull(message = "Tenant role is required.")
    private TenantRole tenantRole;
}
