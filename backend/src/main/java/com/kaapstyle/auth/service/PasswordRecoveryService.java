package com.kaapstyle.auth.service;

import com.kaapstyle.auth.entity.EmailVerificationToken;
import com.kaapstyle.auth.entity.PasswordResetToken;
import com.kaapstyle.auth.entity.User;
import com.kaapstyle.auth.repository.EmailVerificationTokenRepository;
import com.kaapstyle.auth.repository.PasswordResetTokenRepository;
import com.kaapstyle.auth.repository.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;

/**
 * Password recovery and email verification (CR17 -> UC08 -> T03).
 *
 * Tokens are persisted only as SHA-256 hashes. The raw token is handed to the
 * delivery layer through an application event (so no email transport is coupled
 * here). Callers of {@link #resetPassword(String, String)} and
 * {@link #verifyEmail(String)} supply the token hash (see {@link #hashToken(String)}).
 *
 * Assumed entity API (Lombok-style): PasswordResetToken / EmailVerificationToken
 * expose setUser(User), setTokenHash(String), setExpiresAt(LocalDateTime),
 * setUsed(boolean), isUsed(), getExpiresAt(), getUser(); User exposes
 * setPasswordHash(String) and setVerified(boolean). Repositories expose
 * findByTokenHash(String) returning Optional.
 */
@Service
public class PasswordRecoveryService {

    private static final long RESET_TOKEN_MINUTES = 15;
    private static final long VERIFICATION_TOKEN_HOURS = 24;

    /** Published when a reset token has been issued; consumed by the notification layer. */
    public record PasswordResetRequestedEvent(String email, String rawToken, LocalDateTime expiresAt) {
    }

    /** Published when a verification token has been issued; consumed by the notification layer. */
    public record EmailVerificationRequestedEvent(Long userId, String rawToken, LocalDateTime expiresAt) {
    }

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    public PasswordRecoveryService(UserRepository userRepository,
                                   PasswordResetTokenRepository passwordResetTokenRepository,
                                   EmailVerificationTokenRepository emailVerificationTokenRepository,
                                   PasswordEncoder passwordEncoder,
                                   ApplicationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Issues a password reset token if the email belongs to a user.
     * Always returns normally to prevent email enumeration.
     */
    @Transactional
    public void requestPasswordReset(String email) {
        if (email == null || email.isBlank()) {
            return;
        }
        Optional<User> userOpt = userRepository.findByEmail(email.trim());
        if (userOpt.isEmpty()) {
            return;
        }

        String rawToken = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(RESET_TOKEN_MINUTES);

        PasswordResetToken token = new PasswordResetToken();
        token.setUser(userOpt.get());
        token.setTokenHash(hashToken(rawToken));
        token.setExpiresAt(expiresAt);
        token.setUsed(false);
        passwordResetTokenRepository.save(token);

        eventPublisher.publishEvent(new PasswordResetRequestedEvent(email.trim(), rawToken, expiresAt));
    }

    /**
     * Validates the reset token (exists, not expired, not used), updates the
     * user's BCrypt password hash and marks the token as used.
     */
    @Transactional
    public void resetPassword(String tokenHash, String newPassword) {
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("New password must not be blank");
        }
        PasswordResetToken token = passwordResetTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired token"));

        if (token.isUsed() || token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Invalid or expired token");
        }

        User user = token.getUser();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        token.setUsed(true);

        userRepository.save(user);
        passwordResetTokenRepository.save(token);
    }

    /** Creates an email verification token valid for 24 hours. */
    @Transactional
    public void sendVerificationEmail(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String rawToken = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(VERIFICATION_TOKEN_HOURS);

        EmailVerificationToken token = new EmailVerificationToken();
        token.setUser(user);
        token.setTokenHash(hashToken(rawToken));
        token.setExpiresAt(expiresAt);
        token.setUsed(false);
        emailVerificationTokenRepository.save(token);

        eventPublisher.publishEvent(new EmailVerificationRequestedEvent(userId, rawToken, expiresAt));
    }

    /**
     * Validates the verification token, marks the user as verified and the
     * token as used.
     */
    @Transactional
    public void verifyEmail(String tokenHash) {
        EmailVerificationToken token = emailVerificationTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired token"));

        if (token.isUsed() || token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Invalid or expired token");
        }

        User user = token.getUser();
        user.setVerified(true);
        token.setUsed(true);

        userRepository.save(user);
        emailVerificationTokenRepository.save(token);
    }

    /** SHA-256 hex digest of a raw token; use to convert a raw token into its stored hash. */
    public static String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(rawToken.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}