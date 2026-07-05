package com.arsalan.tenanttable.auth.mapper;

import com.arsalan.tenanttable.auth.dto.UserResponseDto;
import com.arsalan.tenanttable.tenant.entity.Tenant;
import com.arsalan.tenanttable.user.entity.User;

public final class UserMapper {

    private UserMapper() {
        // private constructor prevents instantiation
    }

    public static UserResponseDto toUserResponseDto(User user) {

        Tenant tenant = user.getTenant();

        return UserResponseDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .tenantId(tenant.getId())
                .tenantName(tenant.getName())
                .tenantAddress(tenant.getAddress())
                .tenantPhoneNumber(tenant.getPhoneNumber())
                .tenantRole(user.getTenantRole())
                .tenantStatus(tenant.getTenantStatus())
                .planType(tenant.getPlanType())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
