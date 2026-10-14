-- V2: Create user token tables (refresh tokens and TOTP secrets)
-- Target: MySQL 8.x, InnoDB, utf8mb4
-- Depends on: V1 (users table with BIGINT id primary key)

CREATE TABLE user_refresh_tokens (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id      BIGINT       NOT NULL,
    token_hash   VARCHAR(255) NOT NULL UNIQUE,
    device_info  VARCHAR(255) NULL,
    expires_at   TIMESTAMP    NOT NULL,
    is_revoked   BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_refresh_tokens_user
        FOREIGN KEY (user_id) REFERENCES users (id)
        ON DELETE CASCADE,
    INDEX idx_refresh_token_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE user_totp_secrets (
    user_id      BIGINT       PRIMARY KEY,
    secret_key   VARCHAR(255) NOT NULL,
    is_enabled   BOOLEAN      NOT NULL DEFAULT FALSE,
    backup_codes TEXT         NULL,
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_totp_user
        FOREIGN KEY (user_id) REFERENCES users (id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;