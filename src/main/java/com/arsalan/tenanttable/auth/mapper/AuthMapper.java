package com.arsalan.tenanttable.auth.mapper;

import com.arsalan.tenanttable.auth.dto.RegisterResponseDto;
import com.arsalan.tenanttable.tenant.entity.Tenant;
import com.arsalan.tenanttable.user.entity.User;

public final class AuthMapper {

    private AuthMapper() {
        // private constructor prevents instantiation
    }

    public static RegisterResponseDto toUserResponseDto(User user) {

        Tenant tenant = user.getTenant();

        RegisterResponseDto.RegisterResponseDtoBuilder builder = RegisterResponseDto.builder()
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
