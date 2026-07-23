package com.arsalan.tenanttable.mail;

import com.arsalan.tenanttable.exception.EmailSendingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements IEmailService {
    private static final int MAX_RETRIES = 4;
    private static final long RETRY_DELAY = 2000;

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private void sendHtmlEmail(String to, String subject, String template, Context context) {
        try {
            String html = templateEngine.process(template, context);
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);

            mailSender.send(mimeMessage);
        } catch (MailException ex) {
            throw ex;
        } catch (Exception e) {
            throw new EmailSendingException("Unexpected error while sending email", e);
        }
    }

    private Context createOtpContext(String fullName, String otp, Long expiry) {
        Context context = new Context();
        context.setVariable("name", fullName);
        context.setVariable("otp", otp);
        context.setVariable("expiry", expiry);
        return context;
    }

    private Context createStaffInvitationContext(
            String fullName,
            String invitedBy,
            String tenantName,
            String invitationLink,
            int expiryDays
    ) {
        Context context = new Context();
        context.setVariable("name", fullName);
        context.setVariable("invitedBy", invitedBy);
        context.setVariable("tenantName", tenantName);
        context.setVariable("invitationLink", invitationLink);
        context.setVariable("expiryDays", expiryDays);
        return context;
    }

    @Async
    @Retryable(
            retryFor = MailException.class,
            maxAttempts = MAX_RETRIES,
            backoff = @Backoff(delay = RETRY_DELAY, multiplier = 2)
    )
    @Override
    public void sendOtpEmail(String to, String fullName, String otp, Long expiry) {
        Context context = createOtpContext(fullName, otp, expiry);
        sendHtmlEmail(
                to,
                "Your TenantTable Verification Code",
                "email/otp",
                context
        );
    }

    @Async
    @Retryable(
            retryFor = MailException.class,
            maxAttempts = MAX_RETRIES,
            backoff = @Backoff(delay = 3500, multiplier = 2)
    )
    @Override
    public void sendWelcomeEmail(String to, String fullName) {
        Context context = new Context();
        context.setVariable("name", fullName);

        sendHtmlEmail(
                to,
                "Welcome to TenantTable",
                "email/welcome",
                context
        );
    }

    @Async
    @Retryable(
            retryFor = MailException.class,
            maxAttempts = MAX_RETRIES,
            backoff = @Backoff(delay = RETRY_DELAY, multiplier = 2)
    )
    @Override
    public void sendPasswordResetEmail(String to, String fullName, String otp, Long expiry) {
        Context context = createOtpContext(fullName, otp, expiry);

        sendHtmlEmail(
                to,
                "Reset Your TenantTable Password",
                "email/password-reset",
                context
        );
    }

    @Async
    @Retryable(
            retryFor = MailException.class,
            maxAttempts = MAX_RETRIES,
            backoff = @Backoff(delay = RETRY_DELAY, multiplier = 2)
    )
    @Override
    public void sendVerificationEmail(String to, String fullName, String otp, Long expiry) {
        Context context = createOtpContext(fullName, otp, expiry);

        sendHtmlEmail(
                to,
                "Verify Your Email Address",
                "email/verify-email",
                context
        );
    }

    @Override
    @Async
    @Retryable(
            retryFor = MailException.class,
            maxAttempts = MAX_RETRIES,
            backoff = @Backoff(delay = RETRY_DELAY, multiplier = 2)
    )
    public void sendStaffInvitationEmail(
            String to,
            String fullName,
            String invitedBy,
            String tenantName,
            String invitationLink,
            int expiryDays) {

        Context context = createStaffInvitationContext(
                fullName,
                invitedBy,
                tenantName,
                invitationLink,
                expiryDays
        );

        sendHtmlEmail(
                to,
                "You're Invited to Join " + tenantName + " on TenantTable",
                "email/staff-invitation",
                context
        );

    }
}