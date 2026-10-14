package com.kaapstyle.security.service;

import com.kaapstyle.security.entity.AccountLockoutRecord;
import com.kaapstyle.security.entity.IpRateLimitRecord;
import com.kaapstyle.security.repository.AccountLockoutRecordRepository;
import com.kaapstyle.security.repository.IpRateLimitRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

/**
 * Security defense service: IP based rate limiting and account lockout handling.
 *
 * Assumed collaborators (not part of this file):
 *  - IpRateLimitRecordRepository#findByIpAddressAndEndpointGroup(String, String) : Optional<IpRateLimitRecord>
 *  - AccountLockoutRecordRepository#findByUserId(Long) : Optional<AccountLockoutRecord>
 *  - IpRateLimitRecord: ipAddress, endpointGroup, requestCount (int), windowStart (LocalDateTime)
 *  - AccountLockoutRecord: userId, failedAttempts (int), lockedUntil (LocalDateTime)
 */
@Service
public class SecurityDefenseService {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCKOUT_MINUTES = 15L;
    private static final long RATE_WINDOW_SECONDS = 60L;

    private final IpRateLimitRecordRepository ipRateLimitRecordRepository;
    private final AccountLockoutRecordRepository accountLockoutRecordRepository;

    @Autowired
    public SecurityDefenseService(IpRateLimitRecordRepository ipRateLimitRecordRepository,
                                  AccountLockoutRecordRepository accountLockoutRecordRepository) {
        this.ipRateLimitRecordRepository = ipRateLimitRecordRepository;
        this.accountLockoutRecordRepository = accountLockoutRecordRepository;
    }

    /**
     * Checks the per-minute request limit for an IP address and endpoint group.
     *
     * @return true if the request is within the limit
     * @throws ResponseStatusException 429 if the limit is exceeded
     */
    @Transactional
    public boolean checkRateLimit(String ipAddress, String endpointGroup, int maxRequestsPerMinute) {
        LocalDateTime now = LocalDateTime.now();

        IpRateLimitRecord record = ipRateLimitRecordRepository
                .findByIpAddressAndEndpointGroup(ipAddress, endpointGroup)
                .orElse(null);

        if (record == null) {
            record = new IpRateLimitRecord();
            record.setIpAddress(ipAddress);
            record.setEndpointGroup(endpointGroup);
            record.setRequestCount(1);
            record.setWindowStart(now);
            ipRateLimitRecordRepository.save(record);
            return true;
        }

        LocalDateTime windowStart = record.getWindowStart();
        if (windowStart == null || !windowStart.plusSeconds(RATE_WINDOW_SECONDS).isAfter(now)) {
            // Window expired: start a new one
            record.setWindowStart(now);
            record.setRequestCount(1);
            ipRateLimitRecordRepository.save(record);
            return true;
        }

        if (record.getRequestCount() >= maxRequestsPerMinute) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded");
        }

        record.setRequestCount(record.getRequestCount() + 1);
        ipRateLimitRecordRepository.save(record);
        return true;
    }

    /**
     * Records a failed login. After 5 failed attempts the account is locked for 15 minutes.
     */
    @Transactional
    public void recordFailedLogin(Long userId) {
        LocalDateTime now = LocalDateTime.now();

        AccountLockoutRecord record = accountLockoutRecordRepository
                .findByUserId(userId)
                .orElse(null);

        if (record == null) {
            record = new AccountLockoutRecord();
            record.setUserId(userId);
            record.setFailedAttempts(0);
        } else if (record.getLockedUntil() != null && !record.getLockedUntil().isAfter(now)) {
            // Previous lock has expired: start counting afresh
            record.setFailedAttempts(0);
            record.setLockedUntil(null);
        }

        int attempts = record.getFailedAttempts() + 1;
        record.setFailedAttempts(attempts);

        if (attempts >= MAX_FAILED_ATTEMPTS) {
            record.setLockedUntil(now.plusMinutes(LOCKOUT_MINUTES));
        }

        accountLockoutRecordRepository.save(record);
    }

    /**
     * Clears the lockout record after a successful login.
     */
    @Transactional
    public void resetFailedLogins(Long userId) {
        accountLockoutRecordRepository.findByUserId(userId)
                .ifPresent(accountLockoutRecordRepository::delete);
    }

    /**
     * @return true if the account is currently locked
     */
    @Transactional(readOnly = true)
    public boolean isAccountLocked(Long userId) {
        return accountLockoutRecordRepository.findByUserId(userId)
                .map(AccountLockoutRecord::getLockedUntil)
                .map(lockedUntil -> lockedUntil.isAfter(LocalDateTime.now()))
                .orElse(false);
    }
}