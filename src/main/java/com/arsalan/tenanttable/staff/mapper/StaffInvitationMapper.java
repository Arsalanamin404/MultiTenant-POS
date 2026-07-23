package com.arsalan.tenanttable.staff.mapper;

import com.arsalan.tenanttable.staff.dto.StaffInvitationResponseDto;
import com.arsalan.tenanttable.staff.entity.StaffInvitation;

public final class StaffInvitationMapper {
    private StaffInvitationMapper() {
    }

    public static StaffInvitationResponseDto toDto(StaffInvitation invitation) {
        return StaffInvitationResponseDto.builder()
                .fullName(invitation.getFullName())
                .email(invitation.getEmail())
                .tenantRole(invitation.getTenantRole())
                .status(invitation.getStatus())
                .expiresAt(invitation.getExpiresAt())
                .createdAt(invitation.getCreatedAt())
                .build();
    }
}
