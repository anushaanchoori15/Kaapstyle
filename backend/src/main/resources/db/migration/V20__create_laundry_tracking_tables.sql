-- Task: B20-DB-ST01 | Hierarchy: CR05 -> UC01 -> T01
-- Creates the laundry tracking status table for wardrobe items.
-- Encoding: utf8mb4 / utf8mb4_unicode_ci

CREATE TABLE item_laundry_statuses (
  item_id BIGINT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  status VARCHAR(30) NOT NULL DEFAULT 'CLEAN',
  wash_cycle_count INT NOT NULL DEFAULT 0,
  care_instruction_tags JSON NULL,
  last_washed_date DATE NULL,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_laundry_item FOREIGN KEY (item_id) REFERENCES wardrobe_items(id) ON DELETE CASCADE,
  CONSTRAINT fk_laundry_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_laundry_user_status (user_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;