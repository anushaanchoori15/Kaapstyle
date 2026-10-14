-- V13: Wardrobe tags and item-tag mappings
-- Hierarchy: CR02 -> UC09 -> T01 (B13-DB-ST01)
-- Requires: wardrobe_items(id) must be BIGINT and already exist (created in an earlier migration).

CREATE TABLE wardrobe_tags (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  tag_name VARCHAR(50) NOT NULL UNIQUE,
  tag_type VARCHAR(30) NOT NULL DEFAULT 'CUSTOM',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE item_tag_mappings (
  item_id BIGINT NOT NULL,
  tag_id BIGINT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (item_id, tag_id),
  CONSTRAINT fk_map_item FOREIGN KEY (item_id) REFERENCES wardrobe_items(id) ON DELETE CASCADE,
  CONSTRAINT fk_map_tag FOREIGN KEY (tag_id) REFERENCES wardrobe_tags(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO wardrobe_tags (tag_name, tag_type) VALUES
('Casual', 'STYLE'),
('Formal', 'STYLE'),
('Summer', 'SEASON'),
('Winter', 'SEASON'),
('Work', 'OCCASION'),
('Party', 'OCCASION'),
('Sport', 'OCCASION');