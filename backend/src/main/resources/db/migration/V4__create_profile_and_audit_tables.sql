-- Flyway migration V4
-- Hierarchy: CR17 -> UC12 -> T01 (B04-DB-ST01)
-- Creates user profile and security audit log tables (MySQL, utf8mb4).

CREATE TABLE user_profiles (
    user_id                    BIGINT       NOT NULL,
    display_name               VARCHAR(100) NULL,
    avatar_url                 VARCHAR(500) NULL,
    measurement_unit           VARCHAR(10)  NOT NULL DEFAULT 'METRIC',
    theme_mode                 VARCHAR(20)  NOT NULL DEFAULT 'SYSTEM',
    is_family_sharing_enabled  BOOLEAN      NOT NULL DEFAULT FALSE,
    is_telemetry_enabled       BOOLEAN      NOT NULL DEFAULT TRUE,
    updated_at                 TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_user_profiles PRIMARY KEY (user_id),
    CONSTRAINT fk_profile_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE security_audit_logs (
    id           BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id      BIGINT       NOT NULL,
    action_type  VARCHAR(50)  NOT NULL,
    ip_address   VARCHAR(45)  NULL,
    device_info  VARCHAR(255) NULL,
    status       VARCHAR(20)  NOT NULL,
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    INDEX idx_audit_user_action (user_id, action_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;