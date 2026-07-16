package com.arsalan.tenanttable.auth.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RegisterRequestDto {
    // Owner Details
    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name cannot exceed 100 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email address")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 25, message = "Password must contain at least 8 characters")
    private String password;

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Please enter a valid 10 digit phone number"
    )
    private String phoneNumber;

    @NotBlank(message = "Tenant name is required")
    @Size(max = 150, message = "Tenant name cannot exceed 150 characters")
    private String tenantName;

    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Please enter a valid 10 digit phone number"
    )
    private String tenantPhoneNumber;

    @NotBlank(message = "Tenant address is required")
    @Size(max = 500, message = "Tenant address cannot exceed 500 characters")
    private String tenantAddress;

    @NotBlank(message = "Tenant taxRate is required")
    @DecimalMin(value = "0.0", message = "Tax rate cannot be negative")
    @DecimalMax(value = "100.0", message = "Tax rate cannot exceed 100")
    private BigDecimal taxRate;
}
