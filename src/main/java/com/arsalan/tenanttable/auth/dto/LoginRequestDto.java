package com.arsalan.tenanttable.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequestDto {
    @NotBlank(message = "email is required")
    @Email(message = "please enter a valid email")
    private String email;

    @NotBlank(message = "password is required")
    @Size(max = 25,message = "password can not exceed 25 characters")
    private String password;

}
