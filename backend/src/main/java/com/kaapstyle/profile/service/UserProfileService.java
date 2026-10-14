package com.kaapstyle.profile.service;

import com.kaapstyle.profile.entity.SecurityAuditLog;
import com.kaapstyle.profile.entity.UserProfile;
import com.kaapstyle.profile.repository.SecurityAuditLogRepository;
import com.kaapstyle.profile.repository.UserProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service handling user profile management and security audit logging.
 * CR17 -> UC12 -> T03 (B04-JAVA-ST03)
 */
@Service
public class UserProfileService {

    private static final String DEFAULT_MEASUREMENT_UNIT = "METRIC";
    private static final String DEFAULT_THEME_MODE = "SYSTEM";

    private final UserProfileRepository userProfileRepository;
    private final SecurityAuditLogRepository securityAuditLogRepository;

    public UserProfileService(UserProfileRepository userProfileRepository,
                              SecurityAuditLogRepository securityAuditLogRepository) {
        this.userProfileRepository = userProfileRepository;
        this.securityAuditLogRepository = securityAuditLogRepository;
    }

    /**
     * Returns the profile of the given user, creating a default one if none exists.
     */
    @Transactional
    public UserProfile getProfile(Long userId) {
        return userProfileRepository.findByUserId(userId)
                .orElseGet(() -> userProfileRepository.save(createDefaultProfile(userId)));
    }

    /**
     * Updates only the non-null fields of the user's profile, then saves and returns it.
     */
    @Transactional
    public UserProfile updateProfile(Long userId,
                                     String displayName,
                                     String avatarUrl,
                                     String measurementUnit,
                                     String themeMode,
                                     Boolean familySharing,
                                     Boolean telemetry) {
        UserProfile profile = getProfile(userId);

        if (displayName != null) {
            profile.setDisplayName(displayName);
        }
        if (avatarUrl != null) {
            profile.setAvatarUrl(avatarUrl);
        }
        if (measurementUnit != null) {
            profile.setMeasurementUnit(measurementUnit);
        }
        if (themeMode != null) {
            profile.setThemeMode(themeMode);
        }
        if (familySharing != null) {
            profile.setFamilySharing(familySharing);
        }
        if (telemetry != null) {
            profile.setTelemetry(telemetry);
        }

        return userProfileRepository.save(profile);
    }

    /**
     * Persists a security audit log entry.
     */
    @Transactional
    public SecurityAuditLog logSecurityAction(Long userId,
                                              String actionType,
                                              String ipAddress,
                                              String deviceInfo,
                                              String status) {
        SecurityAuditLog log = new SecurityAuditLog();
        log.setUserId(userId);
        log.setActionType(actionType);
        log.setIpAddress(ipAddress);
        log.setDeviceInfo(deviceInfo);
        log.setStatus(status);
        return securityAuditLogRepository.save(log);
    }

    /**
     * Returns all security audit logs for the given user.
     */
    @Transactional(readOnly = true)
    public List<SecurityAuditLog> getAuditLogs(Long userId) {
        return securityAuditLogRepository.findByUserId(userId);
    }

    private UserProfile createDefaultProfile(Long userId) {
        UserProfile profile = new UserProfile();
        profile.setUserId(userId);
        profile.setMeasurementUnit(DEFAULT_MEASUREMENT_UNIT);
        profile.setThemeMode(DEFAULT_THEME_MODE);
        profile.setFamilySharing(false);
        profile.setTelemetry(false);
        return profile;
    }
}