package com.arsalan.tenanttable.staff.repository;

import com.arsalan.tenanttable.staff.entity.StaffInvitation;
import com.arsalan.tenanttable.staff.enums.InvitationStatus;
import com.arsalan.tenanttable.tenant.entity.Tenant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StaffInvitationRepository extends JpaRepository<StaffInvitation, UUID> {

    Optional<StaffInvitation> findByIdAndTenant(UUID id, Tenant tenant);

    Optional<StaffInvitation> findByToken(String token);

    boolean existsByEmailIgnoreCaseAndTenantAndStatus(
            String email,
            Tenant tenant,
            InvitationStatus status
    );

    boolean existsByPhoneNumberAndTenantAndStatus(
            String phoneNumber,
            Tenant tenant,
            InvitationStatus status
    );


    Page<StaffInvitation> findAllByTenant(
            Tenant tenant,
            Pageable pageable
    );

    Page<StaffInvitation> findAllByTenantAndStatus(
            Tenant tenant,
            InvitationStatus status,
            Pageable pageable
    );
}
