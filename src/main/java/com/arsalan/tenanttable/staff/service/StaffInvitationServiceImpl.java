package com.arsalan.tenanttable.staff.service;

import com.arsalan.tenanttable.common.enums.PlatformRole;
import com.arsalan.tenanttable.common.enums.TenantRole;
import com.arsalan.tenanttable.common.utils.HashUtil;
import com.arsalan.tenanttable.common.utils.ICurrentUserUtilService;
import com.arsalan.tenanttable.common.utils.TokenUtil;
import com.arsalan.tenanttable.exception.InvalidInvitationStateException;
import com.arsalan.tenanttable.exception.ResourceAlreadyExistsException;
import com.arsalan.tenanttable.exception.ResourceNotFoundException;
import com.arsalan.tenanttable.mail.IEmailService;
import com.arsalan.tenanttable.staff.dto.AcceptInvitationRequestDto;
import com.arsalan.tenanttable.staff.dto.InviteStaffRequestDto;
import com.arsalan.tenanttable.staff.dto.StaffInvitationResponseDto;
import com.arsalan.tenanttable.staff.entity.StaffInvitation;
import com.arsalan.tenanttable.staff.enums.InvitationStatus;
import com.arsalan.tenanttable.staff.mapper.StaffInvitationMapper;
import com.arsalan.tenanttable.staff.repository.StaffInvitationRepository;
import com.arsalan.tenanttable.tenant.entity.Tenant;
import com.arsalan.tenanttable.tenant.repository.TenantRepository;
import com.arsalan.tenanttable.user.entity.User;
import com.arsalan.tenanttable.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class StaffInvitationServiceImpl implements IStaffInvitationService {
    private final StaffInvitationRepository invitationRepository;
    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final ICurrentUserUtilService currentUserUtilService;
    private final IEmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.invitation.expiration-days}")
    private int invitationExpirationDays;

    @Value("${app.frontend-url}")
    private String frontendUrl;


    private User getOrThrowCurrentUser() {
        UUID userId = currentUserUtilService.getCurrentUserId();

        return userRepository
                .findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("USER_NOT_FOUND with user_id: " + userId));
    }

    private Tenant getOrThrowCurrentTenant() {
        UUID tenantId = currentUserUtilService.getCurrentTenantId();

        return tenantRepository
                .findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("TENANT_NOT_FOUND with tenant_id: " + tenantId));
    }

    private StaffInvitation getOrThrowInvitation(UUID invitationId, Tenant currentTenant) {
        return invitationRepository
                .findByIdAndTenant(invitationId, currentTenant)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "STAFF_INVITATION_NOT_FOUND with invitation_id: " + invitationId
                ));
    }

    private String buildInvitationLink(String token) {
        return frontendUrl + "/accept-invitation?token=" + token;
    }

    private void validatePendingInvitation(@NonNull StaffInvitation invitation) {
        if (invitation.getStatus() == InvitationStatus.CANCELLED) {
            throw new InvalidInvitationStateException("Invitation is already cancelled.");
        }

        if (invitation.getStatus() == InvitationStatus.ACCEPTED) {
            throw new InvalidInvitationStateException("Accepted invitations cannot be modified.");
        }

        if (invitation.isExpired()) {
            throw new InvalidInvitationStateException("Invitation has expired.");
        }
    }

    private void validateInvitationCanBeResent(@NonNull StaffInvitation invitation) {
        if (invitation.getStatus() == InvitationStatus.CANCELLED) {
            throw new InvalidInvitationStateException("Invitation is already cancelled.");
        }

        if (invitation.getStatus() == InvitationStatus.ACCEPTED) {
            throw new InvalidInvitationStateException("Accepted invitations cannot be resent.");
        }
    }

    private void validateInvitationForAcceptance(@NonNull StaffInvitation invitation) {

        if (invitation.getStatus() == InvitationStatus.ACCEPTED) {
            throw new InvalidInvitationStateException(
                    "Invitation has already been accepted."
            );
        }

        if (invitation.getStatus() == InvitationStatus.CANCELLED) {
            throw new InvalidInvitationStateException(
                    "Invitation has been cancelled."
            );
        }

        if (invitation.isExpired()) {
            throw new InvalidInvitationStateException(
                    "Invitation has expired."
            );
        }
    }

    private void validatePasswords(String password, String confirmPassword) {
        if (!password.trim().equals(confirmPassword.trim())) {
            throw new InvalidInvitationStateException(
                    "Passwords do not match."
            );
        }
    }

    private User createUser(StaffInvitation invitation, AcceptInvitationRequestDto dto) {
        return User.builder()
                .fullName(invitation.getFullName())
                .email(invitation.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .phoneNumber(invitation.getPhoneNumber())
                .tenantRole(invitation.getTenantRole())
                .platformRole(PlatformRole.USER)
                .emailVerified(true)
                .tenant(invitation.getTenant())
                .build();
    }

    @Override
    public StaffInvitationResponseDto invite(@NonNull InviteStaffRequestDto dto) {
        User currentUser = getOrThrowCurrentUser();
        Tenant currentTenant = getOrThrowCurrentTenant();
        String email = dto.getEmail().trim().toLowerCase();
        String fullName = dto.getFullName().trim();
        String phoneNumber = dto.getPhoneNumber().trim();

        if (dto.getTenantRole() == TenantRole.OWNER) {
            throw new InvalidInvitationStateException("Owner role cannot be assigned through invitations.");
        }

        if (userRepository.existsByEmail(email)) {
            log.warn("USER_WITH_'{}'_EMAIL_ALREADY_EXISTS", email);
            throw new ResourceAlreadyExistsException("USER_WITH_THIS_EMAIL_ALREADY_EXISTS");
        }

        if (invitationRepository.existsByEmailIgnoreCaseAndTenantAndStatus(
                email,
                currentTenant,
                InvitationStatus.PENDING
        )) {
            log.warn("PENDING_INVITATION_ALREADY_EXISTS for email: {}", email);
            throw new ResourceAlreadyExistsException(
                    "A pending invitation already exists for this email."
            );
        }

        if (invitationRepository.existsByPhoneNumberAndTenantAndStatus(
                phoneNumber,
                currentTenant,
                InvitationStatus.PENDING
        )) {
            log.warn("PENDING_INVITATION_ALREADY_EXISTS for phoneNumber: {}", dto.getPhoneNumber());
            throw new ResourceAlreadyExistsException(
                    "A pending invitation already exists for this phone number."
            );
        }

        if (userRepository.existsByPhoneNumber(dto.getPhoneNumber().trim())) {
            log.warn("USER_WITH_THIS_PHONE_NUMBER_ALREADY_EXISTS: {}", dto.getPhoneNumber());
            throw new ResourceAlreadyExistsException(
                    "USER_WITH_THIS_PHONE_NUMBER_ALREADY_EXISTS"
            );
        }

        String rawToken = TokenUtil.generateToken();
        String hashedToken = HashUtil.sha256(rawToken);

        StaffInvitation invitation = StaffInvitation.builder()
                .fullName(fullName)
                .email(email)
                .phoneNumber(phoneNumber)
                .tenantRole(dto.getTenantRole())
                .token(hashedToken)
                .status(InvitationStatus.PENDING)
                .expiresAt(Instant.now().plus(Duration.ofDays(invitationExpirationDays)))
                .tenant(currentTenant)
                .invitedBy(currentUser)
                .build();

        StaffInvitation savedInvitation = invitationRepository.save(invitation);

        log.info(
                "STAFF_INVITATION_CREATED: invitationId={}, email={}, tenantId={}, invitedBy={}",
                savedInvitation.getId(),
                email,
                currentTenant.getId(),
                currentUser.getId()
        );

        String invitationLink = buildInvitationLink(rawToken);

        emailService.sendStaffInvitationEmail(
                email,
                fullName,
                currentUser.getFullName(),
                currentTenant.getName(),
                invitationLink,
                invitationExpirationDays
        );

        return StaffInvitationMapper.toDto(savedInvitation);
    }

    @Transactional
    @Override
    public StaffInvitationResponseDto resendInvitation(UUID invitationId) {
        Tenant currentTenant = getOrThrowCurrentTenant();
        User currentUser = getOrThrowCurrentUser();

        StaffInvitation invitation = getOrThrowInvitation(invitationId, currentTenant);

        validateInvitationCanBeResent(invitation);

        String rawToken = TokenUtil.generateToken();
        String hashedToken = HashUtil.sha256(rawToken);

        invitation.setToken(hashedToken);
        invitation.setExpiresAt(Instant.now().plus(Duration.ofDays(invitationExpirationDays)));

        String invitationLink = buildInvitationLink(rawToken);

        emailService.sendStaffInvitationEmail(
                invitation.getEmail(),
                invitation.getFullName(),
                currentUser.getFullName(),
                currentTenant.getName(),
                invitationLink,
                invitationExpirationDays
        );

        log.info(
                "STAFF_INVITATION_RESENT: invitationId={}, tenantId={}",
                invitation.getId(),
                currentTenant.getId()
        );

        return StaffInvitationMapper.toDto(invitation);
    }

    @Transactional
    @Override
    public StaffInvitationResponseDto cancelInvitation(UUID invitationId) {
        Tenant currentTenant = getOrThrowCurrentTenant();

        StaffInvitation invitation = getOrThrowInvitation(invitationId, currentTenant);

        validatePendingInvitation(invitation);

        invitation.setStatus(InvitationStatus.CANCELLED);

        invitationRepository.save(invitation);

        log.info(
                "STAFF_INVITATION_CANCELLED: invitationId={}, tenantId={}",
                invitation.getId(),
                currentTenant.getId()
        );

        return StaffInvitationMapper.toDto(invitation);
    }

    @Transactional(readOnly = true)
    @Override
    public StaffInvitationResponseDto getByInvitationId(UUID invitationId) {
        Tenant currentTenant = getOrThrowCurrentTenant();

        StaffInvitation invitation = getOrThrowInvitation(invitationId, currentTenant);

        return StaffInvitationMapper.toDto(invitation);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<StaffInvitationResponseDto> getAllInvitations(Pageable pageable) {
        Tenant currentTenant = getOrThrowCurrentTenant();

        return invitationRepository
                .findAllByTenant(currentTenant, pageable)
                .map(StaffInvitationMapper::toDto);
    }

    @Override
    @Transactional
    public StaffInvitationResponseDto acceptInvitation(AcceptInvitationRequestDto dto) {
        String hashedToken = HashUtil.sha256(dto.getToken());

        StaffInvitation invitation = invitationRepository
                .findByToken(hashedToken)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "INVALID_INVITATION_TOKEN"
                        )
                );

        validateInvitationForAcceptance(invitation);

        validatePasswords(dto.getPassword(), dto.getConfirmPassword());

        if (userRepository.existsByEmail(invitation.getEmail())) {
            throw new ResourceAlreadyExistsException(
                    "USER_WITH_THIS_EMAIL_ALREADY_EXISTS"
            );
        }
        if (userRepository.existsByPhoneNumber(invitation.getPhoneNumber())) {
            throw new ResourceAlreadyExistsException(
                    "USER_WITH_THIS_PHONE_NUMBER_ALREADY_EXISTS"
            );
        }

        User user = createUser(invitation, dto);
        userRepository.save(user);

        log.info(
                "STAFF_ACCOUNT_CREATED: userId={}, tenantId={}, email={}",
                user.getId(),
                invitation.getTenant().getId(),
                user.getEmail()
        );

        invitation.setStatus(InvitationStatus.ACCEPTED);

        log.info(
                "STAFF_INVITATION_ACCEPTED: invitationId={}, userEmail={}, tenantId={}",
                invitation.getId(),
                invitation.getEmail(),
                invitation.getTenant().getId()
        );

        return StaffInvitationMapper.toDto(invitation);
    }
}
