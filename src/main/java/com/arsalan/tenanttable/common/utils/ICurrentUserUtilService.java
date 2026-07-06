package com.arsalan.tenanttable.common.utils;

import com.arsalan.tenanttable.common.enums.PlatformRole;
import com.arsalan.tenanttable.common.enums.TenantRole;
import com.arsalan.tenanttable.tenant.entity.Tenant;
import com.arsalan.tenanttable.user.entity.User;

import java.util.UUID;

public interface ICurrentUserUtilService {
    User getCurrentUser();

    UUID getCurrentUserId();

    Tenant getCurrentTenant();

    UUID getCurrentTenantId();

    TenantRole getCurrentTenantRole();

    PlatformRole getCurrentPlatformRole();

    boolean isOwner();

    boolean isPlatformAdmin();
}
