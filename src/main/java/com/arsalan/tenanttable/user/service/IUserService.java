package com.arsalan.tenanttable.user.service;

import com.arsalan.tenanttable.user.dto.ChangePasswordRequestDto;
import com.arsalan.tenanttable.user.dto.UpdateProfileRequestDto;
import com.arsalan.tenanttable.user.dto.UserResponseDto;

public interface IUserService {
    UserResponseDto getMe();

    UserResponseDto updateMyProfile(UpdateProfileRequestDto dto);

    UserResponseDto changePassword(ChangePasswordRequestDto dto);
}
