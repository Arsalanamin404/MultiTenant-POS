package com.arsalan.tenanttable.auth.service;

import com.arsalan.tenanttable.auth.dto.*;
import com.arsalan.tenanttable.auth.enitity.RefreshToken;
import com.arsalan.tenanttable.auth.enums.OtpPurpose;
import com.arsalan.tenanttable.auth.mapper.UserMapper;
import com.arsalan.tenanttable.auth.security.CustomUserDetails;
import com.arsalan.tenanttable.auth.security.jwt.JwtService;
import com.arsalan.tenanttable.common.enums.PlatformRole;
import com.arsalan.tenanttable.common.enums.TenantRole;
import com.arsalan.tenanttable.exception.*;
import com.arsalan.tenanttable.mail.IEmailService;
import com.arsalan.tenanttable.tenant.entity.Tenant;
import com.arsalan.tenanttable.tenant.enums.PlanType;
import com.arsalan.tenanttable.tenant.enums.TenantStatus;
import com.arsalan.tenanttable.tenant.repository.TenantRepository;
import com.arsalan.tenanttable.user.entity.User;
import com.arsalan.tenanttable.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final IRefreshTokenService refreshTokenService;
    private final IOtpService otpService;
    private final IEmailService emailService;

    @Override
    @Transactional
    public UserResponseDto register(RegisterRequestDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ResourceAlreadyExistsException(
                    "User with email '" + dto.getEmail() + "' already exists"
            );
        }

        if (userRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
            throw new ResourceAlreadyExistsException(
                    "User with phone number '" + dto.getPhoneNumber() + "' already exists"
            );
        }

        if (tenantRepository.existsByNameIgnoreCase(dto.getTenantName())){
            throw new ResourceAlreadyExistsException(
                    "Tenant '" + dto.getTenantName() + "' already exists."
            );
        }

        String tenantPhone = dto.getTenantPhoneNumber();

        if (tenantPhone == null || tenantPhone.isBlank())
            tenantPhone = dto.getPhoneNumber();

        Tenant tenant = Tenant.builder()
                .name(dto.getTenantName())
                .address(dto.getTenantAddress())
                .taxRate(dto.getTaxRate())
                .phoneNumber(tenantPhone)
                .tenantStatus(TenantStatus.TRIAL)
                .planType(PlanType.FREE)
                .trialEndsAt(LocalDateTime.now().plusDays(14))
                .build();

        Tenant savedTenant = tenantRepository.save(tenant);

        User user = User.builder()
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .phoneNumber(dto.getPhoneNumber())
                .platformRole(PlatformRole.USER)
                .tenantRole(TenantRole.OWNER)
                .tenant(savedTenant)
                .build();

        User owner = userRepository.save(user);

        log.info(
                "Tenant '{}' ({}) created. Owner '{}' ({}) registered.",
                savedTenant.getName(),
                savedTenant.getId(),
                owner.getEmail(),
                owner.getId()
        );

        otpService.generateOtp(owner, OtpPurpose.EMAIL_VERIFICATION);

        return UserMapper.toUserResponseDto(owner);
    }

    @Override
    @Transactional
    public void verifyEmail(VerifyEmailRequestDto dto) {
        User user = userRepository.findByEmail(dto.email())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        if (user.isEmailVerified()) {
            throw new EmailAlreadyVerifiedException("Email is already verified.");
        }

        otpService.verifyOtp(user, dto.otp(), OtpPurpose.EMAIL_VERIFICATION);
        log.info("Email '{}' verified successfully.", user.getEmail());
        user.setEmailVerified(true);

        userRepository.save(user);
        emailService.sendWelcomeEmail(user.getEmail(), user.getFullName());
    }

    @Override
    public void resendVerificationOtp(ResendOtpRequestDto dto) {
        User user = userRepository.findByEmail(dto.email())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        if (user.isEmailVerified())
            throw new EmailAlreadyVerifiedException("Email is already verified.");

        otpService.generateOtp(user, OtpPurpose.EMAIL_VERIFICATION);
    }

    @Override
    public AuthResponseDto login(LoginRequestDto dto, ClientInfo clientInfo) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        User user = userDetails.getUser();

        Tenant tenant = user.getTenant();

        if (tenant != null && tenant.getTenantStatus() == TenantStatus.SUSPENDED) {
            throw new TenantSuspendedException("Your organization has been suspended.");
        }

        if (!user.isEmailVerified()) {
            throw new EmailNotVerifiedException("Please verify your email before logging in.");
        }

        Map<String, Object> claims = new HashMap<>();

        claims.put("userId", user.getId().toString());

        if (user.getTenantRole() != null) {
            claims.put("tenantRole", user.getTenantRole().name());
        }
        if (tenant != null) {
            claims.put("tenantId", tenant.getId().toString());
        }

        if (user.getPlatformRole() != null) {
            claims.put("platformRole", user.getPlatformRole().name());
        }

        String accessToken = jwtService.generateAccessToken(claims, userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        refreshTokenService.createRefreshToken(
                userDetails.getUser(),
                refreshToken,
                clientInfo.ipAddress(),
                clientInfo.userAgent()
        );

        log.info("User '{}' logged in successfully from IP [{}].",
                userDetails.getUser().getEmail(),
                clientInfo);

        return AuthResponseDto
                .builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(UserMapper.toUserResponseDto(user))
                .build();

    }

    @Override
    public AuthResponseDto refreshToken(
            String token,
            ClientInfo clientInfo
    ) {
        RefreshToken storedToken = refreshTokenService.verifyRefreshToken(token);

        User user = storedToken.getUser();
        CustomUserDetails userDetails = new CustomUserDetails(user);

        if (!jwtService.validateToken(token, userDetails))
            throw new InvalidRefreshTokenException("Invalid or Expired refresh token.");

        Map<String, Object> claims = new HashMap<>();

        claims.put("userId", user.getId().toString());

        if (user.getPlatformRole() != null) {
            claims.put("platformRole", user.getPlatformRole().name());
        }

        if (user.getTenantRole() != null) {
            claims.put("tenantRole", user.getTenantRole().name());
        }

        if (user.getTenant() != null) {
            claims.put("tenantId", user.getTenant().getId().toString());
        }
        String newAccessToken = jwtService.generateAccessToken(claims, userDetails);
        String newRefreshToken = jwtService.generateRefreshToken(userDetails);

        refreshTokenService.rotateRefreshToken(
                storedToken,
                newRefreshToken,
                clientInfo.ipAddress(),
                clientInfo.userAgent()
        );

        return AuthResponseDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .user(UserMapper.toUserResponseDto(user))
                .build();
    }

    @Override
    public void logout(String refreshToken) {
        refreshTokenService.revokeRefreshToken(refreshToken);
    }

    @Override
    public void logoutAll(User user) {
        refreshTokenService.revokeAllRefreshTokens(user);
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequestDto dto) {
        userRepository.findByEmail(dto.email())
                .ifPresent(user -> {
                    if (user.isEmailVerified()) {
                        otpService.generateOtp(user, OtpPurpose.RESET_PASSWORD);
                    }
                });

    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequestDto dto) {
        User user = userRepository.findByEmail(dto.email())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        if (!user.isEmailVerified())
            throw new EmailNotVerifiedException("Please verify your email before resetting your password");

        otpService.verifyOtp(user, dto.otp(), OtpPurpose.RESET_PASSWORD);

        if (passwordEncoder.matches(dto.newPassword(), user.getPassword()))
            throw new PasswordReuseException("New password must be different from the current password");

        user.setPassword(passwordEncoder.encode(dto.newPassword()));
        userRepository.save(user);

        refreshTokenService.revokeAllRefreshTokens(user);
    }
}