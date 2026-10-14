-- V15: Create outfits and outfit_items tables
-- Task: CR03 -> UC01 -> T01 (B15-DB-ST01)

CREATE TABLE outfits (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  outfit_name VARCHAR(150) NOT NULL,
  occasion VARCHAR(50) NULL,
  season VARCHAR(30) NOT NULL DEFAULT 'ALL_SEASON',
  notes TEXT NULL,
  is_favorite BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_outfit_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_outfit_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE outfit_items (
  outfit_id BIGINT NOT NULL,
  item_id BIGINT NOT NULL,
  position_index INT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (outfit_id, item_id),
  CONSTRAINT fk_outfit_ref FOREIGN KEY (outfit_id) REFERENCES outfits(id) ON DELETE CASCADE,
  CONSTRAINT fk_item_ref FOREIGN KEY (item_id) REFERENCES wardrobe_items(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;