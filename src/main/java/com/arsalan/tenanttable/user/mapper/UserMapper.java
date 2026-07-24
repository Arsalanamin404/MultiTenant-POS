package com.arsalan.tenanttable.user.mapper;

import com.arsalan.tenanttable.user.dto.UserResponseDto;
import com.arsalan.tenanttable.user.entity.User;

public final class UserMapper {
    private UserMapper() {
    }

    public static UserResponseDto toDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .platformRole(user.getPlatformRole())
                .tenantId(user.getTenant().getId())
                .tenantName(user.getTenant().getName())
                .tenantRole(user.getTenantRole())
                .emailVerified(user.isEmailVerified())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
