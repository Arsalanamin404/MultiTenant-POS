package com.arsalan.tenanttable.user.mapper;

import com.arsalan.tenanttable.user.dto.UserResponseDto;
import com.arsalan.tenanttable.user.entity.User;

public final class UserMapper {
    private UserMapper() {
    }

    public static UserResponseDto toDto(User user) {

        var tenant = user.getTenant();

        return UserResponseDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .platformRole(user.getPlatformRole())
                .tenantId(
                        tenant != null ? tenant.getId() : null
                )
                .tenantName(
                        tenant != null ? tenant.getName() : null
                )
                .tenantRole(
                        tenant != null ? user.getTenantRole() : null
                )
                .emailVerified(user.isEmailVerified())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
