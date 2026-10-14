-- V32: Create brand analytics tables
-- Hierarchy: CR11 -> UC09 -> T01 (B32-DB-ST01)

CREATE TABLE brand_analytics_snapshots (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  brand_name VARCHAR(100) NOT NULL,
  item_count INT NOT NULL DEFAULT 1,
  total_spend DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  avg_cpw DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  satisfaction_score DECIMAL(3,2) NOT NULL DEFAULT 5.00,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_brand_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_brand_user (user_id, brand_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;