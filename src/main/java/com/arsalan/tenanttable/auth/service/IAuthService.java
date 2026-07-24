package com.arsalan.tenanttable.auth.service;

import com.arsalan.tenanttable.auth.dto.*;
import com.arsalan.tenanttable.user.entity.User;

public interface IAuthService {
    RegisterResponseDto register(RegisterRequestDto dto);

    void verifyEmail(VerifyEmailRequestDto dto);

    void resendVerificationOtp(ResendOtpRequestDto dto);

    AuthResponseDto login(LoginRequestDto dto, ClientInfo clientInfo);

    AuthResponseDto refreshToken(String token, ClientInfo clientInfo);

    void logout(String refreshToken);

    void logoutAll(User user);

    void resetPassword(ResetPasswordRequestDto dto);

    void forgotPassword(ForgotPasswordRequestDto dto);
}
