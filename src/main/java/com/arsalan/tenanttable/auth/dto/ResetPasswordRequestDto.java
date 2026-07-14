package com.arsalan.tenanttable.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequestDto(
        @NotBlank(message = "Email is required")
        @Email(message = "Please provide a valid email address")
        String email,

        @NotBlank(message = "OTP is required")
        @Pattern(
                regexp = "^\\d{6}$",
                message = "Enter a valid OTP"
        )
        String otp,

        @NotBlank(message = "New password is required")
        @Size(
                min = 8,
                max = 25,
                message = "Password must contain at least 8 characters"
        )
        String newPassword
) {
}
