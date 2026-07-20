package com.arsalan.tenanttable.auth.controller;

import com.arsalan.tenanttable.auth.dto.*;
import com.arsalan.tenanttable.auth.security.CustomUserDetails;
import com.arsalan.tenanttable.auth.service.IAuthService;
import com.arsalan.tenanttable.common.dto.ApiResponse;
import com.arsalan.tenanttable.exception.InvalidRefreshTokenException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final IAuthService authService;

    @Value("${jwt.refresh-token.cookie-name}")
    private String refreshTokenCookieName;

    @Value("${jwt.refresh-expiry}")
    private long refreshTokenExpiration;

    @Value("${cookie.secure}")
    private boolean secureCookie;

    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank())
            return forwarded.split(",")[0];

        return request.getRemoteAddr();
    }

    private String extractRefreshToken(HttpServletRequest request) {
        if (request.getCookies() == null)
            throw new InvalidRefreshTokenException("Refresh token not found.");

        for (Cookie cookie : request.getCookies()) {
            if (refreshTokenCookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        throw new InvalidRefreshTokenException("Refresh token not found.");
    }

    private ResponseCookie buildRefreshCookie(String refreshToken) {
        return ResponseCookie.from(refreshTokenCookieName, refreshToken)
                .httpOnly(true)
                .secure(secureCookie)
                .sameSite("Strict")
                .path("/api/v1/auth/refresh")
                .maxAge(Duration.ofMillis(refreshTokenExpiration))
                .build();
    }

    private ResponseCookie clearRefreshCookie() {
        return ResponseCookie.from(refreshTokenCookieName, "")
                .httpOnly(true)
                .secure(secureCookie)
                .sameSite("Strict")
                .path("/api/v1/auth/refresh")
                .maxAge(Duration.ZERO)
                .build();
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponseDto>> register(
            @Valid @RequestBody RegisterRequestDto dto,
            HttpServletRequest request
    ) {
        log.info("Registration request received for email: {}", dto.getEmail());

        UserResponseDto user = authService.register(dto);

        ApiResponse<UserResponseDto> response =
                ApiResponse.success(
                        HttpStatus.CREATED.value(),
                        "Registration successful. Please verify your email",
                        user,
                        request.getRequestURI()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(
            @Valid @RequestBody VerifyEmailRequestDto dto,
            HttpServletRequest request
    ) {
        log.info("Email verification requested for {}", dto.email());
        authService.verifyEmail(dto);
        log.info("Email verified successfully for {}", dto.email());

        ApiResponse<Void> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "Email verified successfully.",
                null,
                request.getRequestURI()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/resend-verification-otp")
    public ResponseEntity<ApiResponse<Void>> resendVerificationOtp(
            @Valid @RequestBody ResendOtpRequestDto dto,
            HttpServletRequest request
    ) {
        log.info("Resend verification OTP requested for {}", dto.email());
        authService.resendVerificationOtp(dto);
        ApiResponse<Void> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "Verification email sent successfully.",
                null,
                request.getRequestURI()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDto>> login(
            @Valid @RequestBody LoginRequestDto dto,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        log.info(
                "Login request received for {} from IP {}",
                dto.getEmail(),
                getClientIp(request)
        );

        ClientInfo clientInfo = new ClientInfo(getClientIp(request), request.getHeader("User-Agent"));
        AuthResponseDto auth = authService.login(dto,clientInfo);

        ResponseCookie cookie = buildRefreshCookie(auth.getRefreshToken());

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        auth.setRefreshToken(null);

        ApiResponse<AuthResponseDto> apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Login Successful",
                auth,
                request.getRequestURI()
        );
        log.info(
                "User '{}' logged in successfully from IP {}",
                dto.getEmail(),
                getClientIp(request)
        );
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponseDto>> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        log.info(
                "Refresh token request received from IP {}",
                getClientIp(request)
        );
        String refreshToken = extractRefreshToken(request);

        ClientInfo clientInfo = new ClientInfo(
                getClientIp(request),
                request.getHeader("User-Agent")
        );

        AuthResponseDto auth = authService.refreshToken(refreshToken, clientInfo);

        ResponseCookie cookie = buildRefreshCookie(auth.getRefreshToken());

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        auth.setRefreshToken(null);

        log.info(
                "Access token refreshed from IP {}",
                getClientIp(request)
        );

        ApiResponse<AuthResponseDto> apiResponse = ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Token refreshed successfully",
                        auth,
                        request.getRequestURI()
                );

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ){
        log.info(
                "Logout request received from IP {}",
                getClientIp(request)
        );
        String refreshToken = extractRefreshToken(request);
        authService.logout(refreshToken);

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                clearRefreshCookie().toString()
        );
        log.info(
                "User logged out successfully from IP {}",
                getClientIp(request)
        );

        ApiResponse<Void> apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Logged out successfully",
                null,
                request.getRequestURI()
        );

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/logout-all")
    public ResponseEntity<ApiResponse<Void>> logoutFromAllDevices(
            Authentication authentication,
            HttpServletRequest request,
            HttpServletResponse response
    ){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        log.info(
                "Logout from all devices requested by user {}",
                userDetails.getUsername()
        );

        authService.logoutAll(userDetails.getUser());

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                clearRefreshCookie().toString()
        );

        ApiResponse<Void> apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Logged out from all devices",
                null,
                request.getRequestURI()
        );
        log.info(
                "User '{}' logged out",
                authentication.getName()
        );
        return ResponseEntity.ok(apiResponse);
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequestDto dto,
            HttpServletRequest request
    ) {
        log.info(
                "Forgot password request received for {}",
                dto.email()
        );
        authService.forgotPassword(dto);

        ApiResponse<Void> apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "If an account with that email exists, a password reset OTP has been sent",
                null,
                request.getRequestURI()
        );

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Valid @RequestBody ResetPasswordRequestDto dto,
            HttpServletRequest request
    ) {
        log.info(
                "Password reset request received for {}",
                dto.email()
        );
        authService.resetPassword(dto);

        log.info(
                "Password reset successful for {}",
                dto.email()
        );
        ApiResponse<Void> apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Password reset successfully",
                null,
                request.getRequestURI()
        );

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDto>> getMe(HttpServletRequest request){
        UserResponseDto profile = authService.getMe();

        ApiResponse<UserResponseDto> apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "USER_PROFILE_FETCHED_SUCCESSFULLY",
                profile,
                request.getRequestURI()
                ) ;

        return ResponseEntity.ok(apiResponse);
    }

}
