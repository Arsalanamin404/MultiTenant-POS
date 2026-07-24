package com.arsalan.tenanttable.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateUserStatusRequestDto {

    @NotNull
    private final Boolean active;
}