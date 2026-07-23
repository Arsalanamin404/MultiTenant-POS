package com.arsalan.tenanttable.staff.mapper;

import com.arsalan.tenanttable.staff.dto.StaffResponseDto;
import com.arsalan.tenanttable.user.entity.User;

public final class StaffMapper {
    private StaffMapper() {
    }

    public static StaffResponseDto toDto(User user) {
        return StaffResponseDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .tenantRole(user.getTenantRole())
                .active(user.isActive())
                .updatedAt(user.getUpdatedAt())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
