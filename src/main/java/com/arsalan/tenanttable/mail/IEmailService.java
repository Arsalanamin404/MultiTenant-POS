package com.arsalan.tenanttable.mail;

public interface IEmailService {
    void sendOtpEmail(String to, String fullName, String otp, Long expiry);
    void sendWelcomeEmail(String to, String fullName);
    void sendPasswordResetEmail(String to, String fullName, String otp, Long expiry);
    void sendVerificationEmail(String to, String fullName, String otp, Long expiry);
}