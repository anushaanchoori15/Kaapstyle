-- V6: Security defense tables (rate limiting and account lockout)
-- Traceability: CR15 -> UC01 -> T01 (B06-DB-ST01)
-- Target: MySQL 8.x, InnoDB, utf8mb4

CREATE TABLE ip_rate_limit_records (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    ip_address      VARCHAR(45)     NOT NULL,
    endpoint_group  VARCHAR(50)     NOT NULL,
    request_count   INT             NOT NULL DEFAULT 1,
    window_start    TIMESTAMP       NOT NULL,
    blocked_until   TIMESTAMP       NULL,
    PRIMARY KEY (id),
    INDEX idx_ip_endpoint (ip_address, endpoint_group)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE account_lockout_records (
    user_id          BIGINT         NOT NULL,
    failed_attempts  INT            NOT NULL DEFAULT 0,
    locked_until     TIMESTAMP      NULL,
    last_failed_at   TIMESTAMP      NULL,
    updated_at       TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_lockout_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;