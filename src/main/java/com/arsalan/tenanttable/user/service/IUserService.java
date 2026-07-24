package com.arsalan.tenanttable.user.service;

import com.arsalan.tenanttable.user.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IUserService {
    UserResponseDto getMe();

    UserResponseDto updateMyProfile(UpdateProfileRequestDto dto);

    UserResponseDto changePassword(ChangePasswordRequestDto dto);

    Page<UserSummaryResponseDto> getUsers(Pageable pageable);

    UserResponseDto getUserById(UUID id);
    
    UserResponseDto updateUserStatus(UUID userId, UpdateUserStatusRequestDto dto);
}
