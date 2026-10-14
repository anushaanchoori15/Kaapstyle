-- V18: Create item_wear_history table
-- Hierarchy: CR03 -> UC13 -> T01 (B18-DB-ST01)
-- Target: MySQL 8.x, InnoDB, utf8mb4

CREATE TABLE item_wear_history (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  item_id BIGINT NOT NULL,
  outfit_id BIGINT NULL,
  worn_date DATE NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_wear_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_wear_item FOREIGN KEY (item_id) REFERENCES wardrobe_items(id) ON DELETE CASCADE,
  CONSTRAINT fk_wear_outfit FOREIGN KEY (outfit_id) REFERENCES outfits(id) ON DELETE SET NULL,
  INDEX idx_wear_user_item (user_id, item_id),
  INDEX idx_wear_date (worn_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;