-- V19: Capsule wardrobes and their item associations (MySQL 8, InnoDB, utf8mb4)
-- Depends on: users(id), wardrobe_items(id)

CREATE TABLE capsule_wardrobes (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  capsule_name VARCHAR(150) NOT NULL,
  season VARCHAR(30) NOT NULL DEFAULT 'ALL_SEASON',
  target_item_count INT NOT NULL DEFAULT 30,
  description TEXT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_capsule_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_capsule_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE capsule_items (
  capsule_id BIGINT NOT NULL,
  item_id BIGINT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (capsule_id, item_id),
  CONSTRAINT fk_cap_capsule FOREIGN KEY (capsule_id) REFERENCES capsule_wardrobes(id) ON DELETE CASCADE,
  CONSTRAINT fk_cap_item FOREIGN KEY (item_id) REFERENCES wardrobe_items(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;