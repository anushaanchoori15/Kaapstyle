-- V14: Create saved_wardrobe_filters table
-- Traceability: CR02 -> UC13 -> T01 (B14-DB-ST01)

CREATE TABLE saved_wardrobe_filters (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  filter_name VARCHAR(100) NOT NULL,
  category_ids JSON NULL,
  color_hex_list JSON NULL,
  seasons JSON NULL,
  tags JSON NULL,
  sort_by VARCHAR(50) NOT NULL DEFAULT 'CREATED_DESC',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_filter_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_filter_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;