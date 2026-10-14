-- Flyway migration V21: dry cleaning tables
-- Hierarchy: CR05 -> UC05 -> T01 (B21-DB-ST01)
-- Requires existing tables: users(id), wardrobe_items(id)

CREATE TABLE dry_cleaning_orders (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  service_provider_name VARCHAR(150) NOT NULL,
  dropoff_date DATE NOT NULL,
  expected_pickup_date DATE NULL,
  actual_pickup_date DATE NULL,
  total_cost DECIMAL(8,2) NULL,
  order_status VARCHAR(30) NOT NULL DEFAULT 'DROPPED_OFF',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_dry_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_dry_user_status (user_id, order_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE dry_cleaning_items (
  order_id BIGINT NOT NULL,
  item_id BIGINT NOT NULL,
  special_instructions VARCHAR(255) NULL,
  PRIMARY KEY (order_id, item_id),
  CONSTRAINT fk_order_ref FOREIGN KEY (order_id) REFERENCES dry_cleaning_orders(id) ON DELETE CASCADE,
  CONSTRAINT fk_item_ref_dry FOREIGN KEY (item_id) REFERENCES wardrobe_items(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;