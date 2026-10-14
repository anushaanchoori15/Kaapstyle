-- Flyway migration V9
-- Hierarchy: CR01 -> UC09 -> T01 (B09-DB-ST01)
-- Creates the audit table for media sanitization (EXIF/GPS/device info stripping).

CREATE TABLE media_sanitization_logs (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  media_id BIGINT NOT NULL,
  original_has_exif BOOLEAN NOT NULL DEFAULT FALSE,
  gps_stripped BOOLEAN NOT NULL DEFAULT FALSE,
  device_info_stripped BOOLEAN NOT NULL DEFAULT FALSE,
  sanitized_mime_type VARCHAR(100) NOT NULL,
  sanitized_bytes BIGINT NOT NULL,
  sanitized_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_sanitization_media FOREIGN KEY (media_id) REFERENCES media_files(id) ON DELETE CASCADE,
  INDEX idx_sanitization_media (media_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;