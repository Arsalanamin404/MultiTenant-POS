package com.arsalan.tenanttable.staff.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AcceptInvitationRequestDto {
    @NotBlank(message = "Invitation token is required.")
    private String token;

    @NotBlank(message = "Password is required.")
    @Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters.")
    private String password;

    @NotBlank(message = "Confirm password is required.")
    private String confirmPassword;
}
