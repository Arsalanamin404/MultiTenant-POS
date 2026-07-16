package com.arsalan.tenanttable.common.utils;

import com.arsalan.tenanttable.common.enums.PlatformRole;
import com.arsalan.tenanttable.common.enums.TenantRole;

import java.util.UUID;

public interface ICurrentUserUtilService {
    UUID getCurrentUserId();

    UUID getCurrentTenantId();

    TenantRole getCurrentTenantRole();

    PlatformRole getCurrentPlatformRole();

    boolean isOwner();

    boolean isPlatformAdmin();
}
