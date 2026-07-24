package com.arsalan.tenanttable.user.mapper;

import com.arsalan.tenanttable.user.dto.UserSummaryResponseDto;
import com.arsalan.tenanttable.user.entity.User;

public final class UserSummaryMapper {
    private UserSummaryMapper() {
    }

    public static UserSummaryResponseDto toDto(User user) {
        return UserSummaryResponseDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .platformRole(user.getPlatformRole())
                .tenantId(user.getTenant().getId())
                .tenantName(user.getTenant().getName())
                .active(user.isActive())
                .emailVerified(user.isEmailVerified())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
