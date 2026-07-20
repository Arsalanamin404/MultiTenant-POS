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

        UserResponseDto.UserResponseDtoBuilder builder = UserResponseDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .platformRole(user.getPlatformRole())
                .tenantRole(user.getTenantRole())
                .createdAt(user.getCreatedAt());
        if (tenant != null) {
            builder
                    .tenantId(tenant.getId())
                    .tenantName(tenant.getName())
                    .tenantAddress(tenant.getAddress())
                    .tenantPhoneNumber(tenant.getPhoneNumber())
                    .taxRate(tenant.getTaxRate())
                    .tenantStatus(tenant.getTenantStatus())
                    .planType(tenant.getPlanType());
        }

        return builder.build();
    }
}
