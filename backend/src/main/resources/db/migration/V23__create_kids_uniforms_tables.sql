-- Flyway migration V23: kids profiles and school uniform schedules
-- Hierarchy: CR07 -> UC01 -> T01 (B23-DB-ST01)
-- Target: MySQL 8.x, InnoDB, utf8mb4

CREATE TABLE kids_profiles (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  guardian_id BIGINT NOT NULL,
  child_name VARCHAR(100) NOT NULL,
  birth_date DATE NULL,
  school_name VARCHAR(150) NULL,
  grade_level VARCHAR(30) NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_kid_guardian FOREIGN KEY (guardian_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_kid_guardian (guardian_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE school_uniform_schedules (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  kid_id BIGINT NOT NULL,
  day_of_week VARCHAR(15) NOT NULL,
  uniform_type VARCHAR(50) NOT NULL DEFAULT 'STANDARD',
  notes VARCHAR(255) NULL,
  CONSTRAINT fk_uni_kid FOREIGN KEY (kid_id) REFERENCES kids_profiles(id) ON DELETE CASCADE,
  UNIQUE KEY uq_kid_day (kid_id, day_of_week)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;