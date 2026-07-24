package com.arsalan.tenanttable.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequestDto {
    @NotBlank
    @Size(max = 100, message = "Name cannot exceed more than 100 characters")
    String fullName;

    @NotBlank
    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Please enter a valid 10 digit phone number"
    )
    String phoneNumber;
}
