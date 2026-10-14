-- Flyway migration V22: create body_measurements table
-- Hierarchy: CR06 -> UC01 -> T01 (B22-DB-ST01)
-- Target: MySQL 8.x, InnoDB, utf8mb4

CREATE TABLE body_measurements (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  profile_tag VARCHAR(50) NOT NULL DEFAULT 'SELF',
  chest_cm DECIMAL(5,2) NULL,
  waist_cm DECIMAL(5,2) NULL,
  hips_cm DECIMAL(5,2) NULL,
  inseam_cm DECIMAL(5,2) NULL,
  shoulder_width_cm DECIMAL(5,2) NULL,
  recorded_at DATE NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_meas_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_meas_user_profile (user_id, profile_tag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;