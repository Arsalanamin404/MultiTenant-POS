package com.arsalan.tenanttable.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class ChangePasswordRequestDto {
    @NotBlank(message = "Current password is required.")
    private String currentPassword;

    @NotBlank(message = "Password is required.")
    @Size(min = 8, max = 50, message = "New password must be between 8 and 50 characters.")
    private String newPassword;

    @NotBlank(message = "Confirm password is required.")
    private String confirmPassword;
}
