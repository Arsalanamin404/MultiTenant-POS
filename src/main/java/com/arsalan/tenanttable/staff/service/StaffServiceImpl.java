package com.arsalan.tenanttable.staff.service;

import com.arsalan.tenanttable.auth.repository.RefreshTokenRepository;
import com.arsalan.tenanttable.common.enums.TenantRole;
import com.arsalan.tenanttable.common.utils.ICurrentUserUtilService;
import com.arsalan.tenanttable.exception.BusinessOperationException;
import com.arsalan.tenanttable.exception.InvalidInvitationStateException;
import com.arsalan.tenanttable.exception.ResourceNotFoundException;
import com.arsalan.tenanttable.staff.dto.StaffResponseDto;
import com.arsalan.tenanttable.staff.dto.UpdateStaffRoleRequestDto;
import com.arsalan.tenanttable.staff.dto.UpdateStaffStatusRequestDto;
import com.arsalan.tenanttable.staff.mapper.StaffMapper;
import com.arsalan.tenanttable.tenant.entity.Tenant;
import com.arsalan.tenanttable.tenant.repository.TenantRepository;
import com.arsalan.tenanttable.user.entity.User;
import com.arsalan.tenanttable.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class StaffServiceImpl implements IStaffService {
    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final ICurrentUserUtilService currentUserUtilService;
    private final RefreshTokenRepository refreshTokenRepository;

    private @NonNull User getOrThrowCurrentUser() {
        UUID userId = currentUserUtilService.getCurrentUserId();

        return userRepository
                .findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("USER_NOT_FOUND with user_id: " + userId));
    }

    private @NonNull Tenant getOrThrowCurrentTenant() {
        UUID tenantId = currentUserUtilService.getCurrentTenantId();

        return tenantRepository
                .findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("TENANT_NOT_FOUND with tenant_id: " + tenantId));
    }

    private @NonNull User getOrThrowStaff(UUID staffId, Tenant tenant) {
        return userRepository
                .findByIdAndTenant(staffId, tenant)
                .orElseThrow(() -> new ResourceNotFoundException("STAFF_NOT_FOUND with staff_id: " + staffId + "and tenant_id: " + tenant.getId()));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StaffResponseDto> getAllStaff(Pageable pageable) {
        Tenant currentTenant = getOrThrowCurrentTenant();

        return userRepository
                .findAllByTenant(currentTenant, pageable)
                .map(StaffMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public StaffResponseDto getStaffById(UUID staffId) {
        Tenant currentTenant = getOrThrowCurrentTenant();

        User staff = getOrThrowStaff(staffId, currentTenant);

        return StaffMapper.toDto(staff);
    }

    @Override
    @Transactional
    public StaffResponseDto updateRole(UUID staffId, UpdateStaffRoleRequestDto dto) {
        Tenant currentTenant = getOrThrowCurrentTenant();

        User staff = getOrThrowStaff(staffId, currentTenant);

        if (dto.getTenantRole() == TenantRole.OWNER) {
            throw new InvalidInvitationStateException("Owner role cannot be assigned.");
        }

        if (staff.getTenantRole() == dto.getTenantRole()) {
            throw new InvalidInvitationStateException("Staff already has " + dto.getTenantRole() + " role.");
        }

        staff.setTenantRole(dto.getTenantRole());

        return StaffMapper.toDto(staff);
    }

    @Override
    @Transactional
    public StaffResponseDto updateStatus(UUID staffId, UpdateStaffStatusRequestDto dto) {
        Tenant currentTenant = getOrThrowCurrentTenant();

        User currentUser = getOrThrowCurrentUser();

        User staff = getOrThrowStaff(staffId, currentTenant);

        if (staff.getId().equals(currentUser.getId())) {
            throw new BusinessOperationException("You cannot deactivate your own account.");
        }

        if (staff.getTenantRole() == TenantRole.OWNER) {
            throw new BusinessOperationException("Owner account status cannot be changed.");
        }

        if (staff.isActive() == dto.getActive()) {
            throw new BusinessOperationException(
                    dto.getActive()
                            ? "Staff account is already active."
                            : "Staff account is already inactive."
            );
        }

        staff.setActive(dto.getActive());

        if (!dto.getActive()) {
            refreshTokenRepository.deleteAllByUser(staff);
        }

        return StaffMapper.toDto(staff);
    }

}
