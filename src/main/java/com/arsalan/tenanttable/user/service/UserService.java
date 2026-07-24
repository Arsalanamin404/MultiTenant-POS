package com.arsalan.tenanttable.user.service;

import com.arsalan.tenanttable.auth.repository.RefreshTokenRepository;
import com.arsalan.tenanttable.common.utils.ICurrentUserUtilService;
import com.arsalan.tenanttable.exception.InvalidOperationException;
import com.arsalan.tenanttable.exception.ResourceAlreadyExistsException;
import com.arsalan.tenanttable.exception.ResourceNotFoundException;
import com.arsalan.tenanttable.user.dto.*;
import com.arsalan.tenanttable.user.entity.User;
import com.arsalan.tenanttable.user.mapper.UserMapper;
import com.arsalan.tenanttable.user.mapper.UserSummaryMapper;
import com.arsalan.tenanttable.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final ICurrentUserUtilService currentUserUtilService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    private User getOrThrowCurrentUser() {
        UUID userId = currentUserUtilService.getCurrentUserId();

        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("USER_NOT_FOUND"));
    }

    private User getOrThrowUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("USER_NOT_FOUND"));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getMe() {
        User currentUser = getOrThrowCurrentUser();

        log.info("USER_PROFILE_FETCHED_SUCCESSFULLY_FOR_USER_ID='{}'", currentUser.getId());

        return UserMapper.toDto(currentUser);
    }

    @Override
    @Transactional
    public UserResponseDto updateMyProfile(@NonNull UpdateProfileRequestDto dto) {
        User currentUser = getOrThrowCurrentUser();

        log.info("Updating profile for userId={}", currentUser.getId());

        if (!currentUser.getPhoneNumber().equals(dto.getPhoneNumber())) {
            if (userRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
                log.warn("Profile update failed for userId={}: phone number already in use", currentUser.getId());
                throw new ResourceAlreadyExistsException("Phone number is already in use.");
            }

            log.debug("Updating phone number for userId={}", currentUser.getId());
            currentUser.setPhoneNumber(dto.getPhoneNumber());
        }

        currentUser.setFullName(dto.getFullName());

        User updatedUser = userRepository.save(currentUser);

        log.info("Profile updated successfully for userId={}", updatedUser.getId());

        return UserMapper.toDto(updatedUser);
    }

    @Override
    @Transactional
    public UserResponseDto changePassword(ChangePasswordRequestDto dto) {
        User currentUser = getOrThrowCurrentUser();

        log.info("Password change requested for userId={}", currentUser.getId());

        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            log.warn("Password change failed for userId={}: new password and confirmation do not match", currentUser.getId());
            throw new InvalidOperationException("New password and confirm password do not match.");
        }

        if (!passwordEncoder.matches(dto.getCurrentPassword(), currentUser.getPassword())) {
            log.warn("Password change failed for userId={}: current password is incorrect", currentUser.getId());
            throw new InvalidOperationException("Current password is incorrect.");
        }

        if (passwordEncoder.matches(dto.getNewPassword(), currentUser.getPassword())) {
            log.warn("Password change failed for userId={}: new password same as current", currentUser.getId());
            throw new InvalidOperationException("New password must be different from the current password.");
        }

        currentUser.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        User updatedUser = userRepository.save(currentUser);

        log.debug("Password hash updated for userId={}", updatedUser.getId());

        refreshTokenRepository.deleteAllByUser(currentUser);
        log.info("PASSWORD_CHANGED_SUCCESSFULLY_FOR_USER_ID='{}'. All refresh tokens revoked.", updatedUser.getId());

        return UserMapper.toDto(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserSummaryResponseDto> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(UserSummaryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(UUID id) {
        User user = getOrThrowUser(id);
        return UserMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserResponseDto updateUserStatus(UUID userId, UpdateUserStatusRequestDto dto) {
        User currentUser = getOrThrowCurrentUser();
        User targetUser = getOrThrowUser(userId);

        if (currentUser.getId().equals(targetUser.getId())) {
            throw new InvalidOperationException("You cannot change your own account status.");
        }

        if (targetUser.isActive() == dto.getActive()) {
            throw new InvalidOperationException("User is already " + (dto.getActive() ? "active." : "inactive."));
        }

        targetUser.setActive(dto.getActive());

        if (!dto.getActive()) {
            refreshTokenRepository.deleteAllByUser(targetUser);
        }

        log.info(
                "User status updated by admin. adminId={}, userId={}, active={}",
                currentUser.getId(),
                targetUser.getId(),
                dto.getActive()
        );

        User updatedUser = userRepository.save(targetUser);

        return UserMapper.toDto(updatedUser);
    }
}