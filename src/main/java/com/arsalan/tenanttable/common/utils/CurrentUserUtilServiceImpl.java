package com.arsalan.tenanttable.common.utils;

import com.arsalan.tenanttable.auth.security.CustomUserDetails;
import com.arsalan.tenanttable.common.enums.PlatformRole;
import com.arsalan.tenanttable.common.enums.TenantRole;
import com.arsalan.tenanttable.exception.UnauthorizedException;
import com.arsalan.tenanttable.tenant.entity.Tenant;
import com.arsalan.tenanttable.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class CurrentUserUtilServiceImpl implements ICurrentUserUtilService {
    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken
        ) {
            log.warn("Attempt to access authenticated user without a valid authentication context.");
            throw new UnauthorizedException("User is not authenticated.");
        }
        return ((CustomUserDetails) authentication.getPrincipal()).getUser();
    }

    @Override
    public UUID getCurrentUserId() {
        return getCurrentUser().getId();
    }

    @Override
    public Tenant getCurrentTenant() {
        return getCurrentUser().getTenant();
    }

    @Override
    public UUID getCurrentTenantId() {
        return getCurrentTenant().getId();
    }

    @Override
    public TenantRole getCurrentTenantRole() {
        return getCurrentUser().getTenantRole();
    }

    @Override
    public PlatformRole getCurrentPlatformRole() {
        return getCurrentUser().getPlatformRole();
    }

    @Override
    public boolean isOwner() {
        return getCurrentTenantRole() == TenantRole.OWNER;
    }

    @Override
    public boolean isPlatformAdmin() {
        return getCurrentUser().getPlatformRole() == PlatformRole.SUPER_ADMIN;
    }
}
