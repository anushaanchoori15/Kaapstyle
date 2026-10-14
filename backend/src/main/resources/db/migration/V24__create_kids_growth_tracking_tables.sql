-- Flyway V24: kids growth tracking
-- Hierarchy: CR07 -> UC05 -> T01 (B24-DB-ST01)
-- Target: MySQL 8.x, InnoDB, utf8mb4

CREATE TABLE kids_growth_logs (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  kid_id BIGINT NOT NULL,
  height_cm DECIMAL(5,2) NOT NULL,
  weight_kg DECIMAL(5,2) NULL,
  shoe_size VARCHAR(10) NULL,
  clothing_size_tag VARCHAR(20) NULL,
  recorded_at DATE NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_growth_kid FOREIGN KEY (kid_id) REFERENCES kids_profiles(id) ON DELETE CASCADE,
  INDEX idx_growth_kid_date (kid_id, recorded_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;