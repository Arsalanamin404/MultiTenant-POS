package com.arsalan.tenanttable.administration.tenant.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record OwnerSummaryDto(
        UUID id,
        String fullName,
        String email,
        String phoneNumber

) {
}
