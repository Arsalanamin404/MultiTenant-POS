package com.arsalan.tenanttable.common.utils;

import com.arsalan.tenanttable.auth.security.CustomUserDetails;
import com.arsalan.tenanttable.common.enums.PlatformRole;
import com.arsalan.tenanttable.common.enums.TenantRole;
import com.arsalan.tenanttable.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class CurrentUserUtilServiceImpl implements ICurrentUserUtilService {
    private CustomUserDetails getPrincipal() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {

            log.warn("Attempt to access authenticated user without a valid authentication context.");
            throw new UnauthorizedException("User is not authenticated.");
        }

        return (CustomUserDetails) authentication.getPrincipal();
    }

    @Override
    public UUID getCurrentUserId() {
        return getPrincipal().getUser().getId();
    }


    @Override
    public UUID getCurrentTenantId() {
        return getPrincipal().getUser().getTenant().getId();
    }

    @Override
    public TenantRole getCurrentTenantRole() {
        return getPrincipal().getUser().getTenantRole();
    }

    @Override
    public PlatformRole getCurrentPlatformRole() {
        return getPrincipal().getUser().getPlatformRole();
    }

    @Override
    public boolean isOwner() {
        return getCurrentTenantRole() == TenantRole.OWNER;
    }

    @Override
    public boolean isPlatformAdmin() {
        return getPrincipal().getUser().getPlatformRole() == PlatformRole.SUPER_ADMIN;
    }
}
