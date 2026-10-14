-- V12: Create wardrobe_items table
-- Hierarchy: CR02 -> UC05 -> T01 (B12-DB-ST01)
-- Encoding: utf8mb4 / utf8mb4_unicode_ci

CREATE TABLE wardrobe_items (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  category_id BIGINT NOT NULL,
  media_id BIGINT NULL,
  title VARCHAR(150) NOT NULL,
  color_hex VARCHAR(20) NULL,
  season VARCHAR(30) NOT NULL DEFAULT 'ALL_SEASON',
  brand VARCHAR(100) NULL,
  size_label VARCHAR(30) NULL,
  is_favorite BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_item_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_item_category FOREIGN KEY (category_id) REFERENCES clothing_categories(id) ON DELETE RESTRICT,
  CONSTRAINT fk_item_media FOREIGN KEY (media_id) REFERENCES media_files(id) ON DELETE SET NULL,
  INDEX idx_item_user_cat (user_id, category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;