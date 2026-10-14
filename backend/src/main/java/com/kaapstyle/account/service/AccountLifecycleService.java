package com.kaapstyle.account.service;

import com.kaapstyle.account.entity.GdprExportRequest;
import com.kaapstyle.account.entity.User;
import com.kaapstyle.account.repository.GdprExportRequestRepository;
import com.kaapstyle.account.repository.UserProfileRepository;
import com.kaapstyle.account.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Handles the account lifecycle: soft deletion with a grace period,
 * deletion cancellation, GDPR data export requests and the permanent purge
 * of accounts whose grace period has expired.
 */
@Service
public class AccountLifecycleService {

    private static final Logger log = LoggerFactory.getLogger(AccountLifecycleService.class);

    /** Grace period between soft delete and permanent hard delete. */
    static final Duration RETENTION_PERIOD = Duration.ofDays(30);

    static final String STATUS_PENDING = "PENDING";

    private final UserRepository userRepository;
    private final GdprExportRequestRepository gdprExportRequestRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AccountLifecycleService(UserRepository userRepository,
                                   GdprExportRequestRepository gdprExportRequestRepository,
                                   UserProfileRepository userProfileRepository,
                                   PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.gdprExportRequestRepository = gdprExportRequestRepository;
        this.userProfileRepository = userProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Soft-deletes the account after verifying the password and revokes all issued tokens.
     *
     * @return the instant at which the account will be permanently purged
     */
    @Transactional
    public Instant requestAccountDeletion(Long userId, String confirmationPassword) {
        User user = findUser(userId);

        if (confirmationPassword == null
                || !passwordEncoder.matches(confirmationPassword, user.getPasswordHash())) {
            throw new BadCredentialsException("Password confirmation failed");
        }

        Instant now = Instant.now();
        user.setDeletedAt(now);
        // Token revocation: every token issued before this instant is rejected.
        user.setTokensRevokedAt(now);
        userRepository.save(user);

        return now.plus(RETENTION_PERIOD);
    }

    /** Cancels a pending deletion by clearing the soft-delete marker. */
    @Transactional
    public void cancelAccountDeletion(Long userId) {
        User user = findUser(userId);
        user.setDeletedAt(null);
        userRepository.save(user);
    }

    /** Creates a new GDPR export request with status PENDING. */
    @Transactional
    public GdprExportRequest requestGdprExport(Long userId) {
        findUser(userId);

        GdprExportRequest request = new GdprExportRequest();
        request.setUserId(userId);
        request.setStatus(STATUS_PENDING);
        request.setRequestedAt(Instant.now());
        return gdprExportRequestRepository.save(request);
    }

    /** Returns the latest GDPR export request of the user. */
    @Transactional(readOnly = true)
    public GdprExportRequest getExportStatus(Long userId) {
        return gdprExportRequestRepository.findFirstByUserIdOrderByRequestedAtDesc(userId)
                .orElseThrow(() -> new NoSuchElementException(
                        "No GDPR export request found for user " + userId));
    }

    /**
     * Permanently deletes users soft-deleted more than 30 days ago.
     * Runs daily at 03:00 and may also be invoked manually.
     *
     * @return number of purged accounts
     */
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public int purgeExpiredAccounts() {
        Instant threshold = Instant.now().minus(RETENTION_PERIOD);
        List<User> expired = userRepository.findByDeletedAtBefore(threshold);

        for (User user : expired) {
            Long id = user.getId();
            gdprExportRequestRepository.deleteByUserId(id);
            userProfileRepository.deleteByUserId(id);
            userRepository.delete(user);
        }

        if (!expired.isEmpty()) {
            log.info("Purged {} expired account(s)", expired.size());
        }
        return expired.size();
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));
    }
}