package com.arsalan.tenanttable.exception;

import com.arsalan.tenanttable.common.dto.ApiResponse;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PasswordReuseException.class)
    public ResponseEntity<ApiResponse<Object>> handlePasswordReuse(
            PasswordReuseException ex,
            HttpServletRequest request
    ) {
        log.warn(
                "Password reuse attempt at [{}]. {}",
                request.getRequestURI(),
                ex.getMessage()
        );
        ApiResponse<Object> response = ApiResponse.failure(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                null,
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidRefreshToken(
            InvalidRefreshTokenException ex,
            HttpServletRequest request
    ) {
        log.warn(
                "Invalid refresh token from IP [{}] for [{}].",
                request.getRemoteAddr(),
                request.getRequestURI()
        );
        ApiResponse<Object> response = ApiResponse.failure(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                null,
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Object>> handleUnauthorizedException(
            UnauthorizedException ex,
            HttpServletRequest request
    ) {
        log.warn(
                "Unauthorized user tried to login from IP [{}] for [{}].",
                request.getRemoteAddr(),
                request.getRequestURI()
        );
        ApiResponse<Object> response = ApiResponse.failure(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                null,
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(TenantSuspendedException.class)
    public ResponseEntity<ApiResponse<Object>> handleTenantSuspendedException(
            TenantSuspendedException ex,
            HttpServletRequest request
    ) {
        log.warn(
                "Suspended tenant tried to login from IP [{}] for [{}].",
                request.getRemoteAddr(),
                request.getRequestURI()
        );
        ApiResponse<Object> response = ApiResponse.failure(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                null,
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(EmailAlreadyVerifiedException.class)
    public ResponseEntity<ApiResponse<Object>> handleEmailAlreadyVerified(
            EmailAlreadyVerifiedException ex,
            HttpServletRequest request
    ) {
        log.info(
                "Email already verified. {}",
                ex.getMessage()
        );
        ApiResponse<Object> response = ApiResponse.failure(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                null,
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailNotVerifiedException.class)
    public ResponseEntity<ApiResponse<Object>> handleEmailNotVerified(
            EmailNotVerifiedException ex,
            HttpServletRequest request
    ) {
        log.warn("Email verification required. {}", ex.getMessage());
        ApiResponse<Object> response = ApiResponse.failure(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                null,
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(EmailSendingException.class)
    public ResponseEntity<ApiResponse<Object>> handleEmailSending(
            EmailSendingException ex,
            HttpServletRequest request
    ) {
        log.error(
                "Failed to send email at [{}].",
                request.getRequestURI(),
                ex
        );
        ApiResponse<Object> response = ApiResponse.failure(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage(),
                null,
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(OtpAttemptsExceededException.class)
    public ResponseEntity<ApiResponse<Object>> handleOtpAttemptsExceeded(
            OtpAttemptsExceededException ex,
            HttpServletRequest request
    ) {
        log.warn("OTP attempts exceeded for request [{}].", request.getRequestURI());
        ApiResponse<Object> response = ApiResponse.failure(
                HttpStatus.TOO_MANY_REQUESTS.value(),
                ex.getMessage(),
                null,
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidOtp(
            InvalidOtpException ex,
            HttpServletRequest request
    ) {
        log.warn("Invalid OTP submitted for request [{}].", request.getRequestURI());
        ApiResponse<Object> response = ApiResponse.failure(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                null,
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceAlreadyExist(
            ResourceAlreadyExistsException ex,
            HttpServletRequest request
    ) {
        log.warn("Resource already exists. {}", ex.getMessage());
        ApiResponse<Object> response = ApiResponse.failure(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                null,
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request
    ) {
        log.warn("Resource not found. {}", ex.getMessage());
        ApiResponse<Object> response = ApiResponse.failure(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                null,
                request.getRequestURI()
        );
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
        log.warn("Invalid Credentials from IP [{}].", request.getRemoteAddr());
        ApiResponse<Object> response = ApiResponse.failure(
                HttpStatus.UNAUTHORIZED.value(),
                "Invalid Credentials",
                null,
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error(
                "Unhandled exception at [{} {}]",
                request.getMethod(),
                request.getRequestURI(),
                ex
        );
        ApiResponse<Object> response = ApiResponse.failure(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                null,
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        log.warn("Validation failed for [{}].", request.getRequestURI());
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> {
                    log.warn(
                            "Validation Error - Field: {}, Message: {}",
                            error.getField(),
                            error.getDefaultMessage()
                    );
                    errors.put(
                            error.getField(),
                            error.getDefaultMessage()
                    );
                });
        ApiResponse<Object> response = ApiResponse.failure(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                errors,
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Object>> handleMissingRequestParam(
            MissingServletRequestParameterException ex,
            HttpServletRequest request
    ) {
        log.warn(
                "Missing request parameter '{}' for [{}].",
                ex.getParameterName(),
                request.getRequestURI()
        );
        ApiResponse<Object> response = ApiResponse.failure(
                HttpStatus.BAD_REQUEST.value(),
                ex.getParameterName() + " parameter is required",
                null,
                request.getRequestURI()
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthenticationCredentialsNotFound(
            AuthenticationCredentialsNotFoundException ex,
            HttpServletRequest request
    ) {
        log.warn(
                "Authentication credentials missing for [{}].",
                request.getRequestURI()
        );
        ApiResponse<Object> response = ApiResponse.failure(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                null,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }


    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request
    ) {
        log.warn(
                "Access denied to [{}] from IP [{}].",
                request.getRequestURI(),
                request.getRemoteAddr()
        );
        ApiResponse<Object> response = ApiResponse.failure(
                HttpStatus.FORBIDDEN.value(),
                "Access denied",
                null,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(response);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponse<Object>> handleJwtException(
            JwtException ex,
            HttpServletRequest request
    ) {
        log.warn(
                "Invalid or expired JWT from IP [{}].",
                request.getRemoteAddr()
        );
        ApiResponse<Object> response = ApiResponse.failure(
                HttpStatus.UNAUTHORIZED.value(),
                "Invalid or expired token",
                null,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }
}
