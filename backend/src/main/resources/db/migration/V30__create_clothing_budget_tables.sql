-- ============================================================================
-- V30: Clothing budget tables
-- Hierarchy: CR11 -> UC01 -> T01 (B30-DB-ST01)
-- Creates wardrobe_expenses to track user clothing spending.
-- Engine: InnoDB | Charset: utf8mb4 | Collation: utf8mb4_unicode_ci
-- ============================================================================

CREATE TABLE wardrobe_expenses (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  item_id BIGINT NULL,
  amount DECIMAL(10,2) NOT NULL,
  currency VARCHAR(10) NOT NULL DEFAULT 'USD',
  expense_category VARCHAR(50) NOT NULL DEFAULT 'PURCHASE',
  purchase_date DATE NOT NULL,
  retailer_name VARCHAR(150) NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_exp_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_exp_item FOREIGN KEY (item_id) REFERENCES wardrobe_items(id) ON DELETE SET NULL,
  INDEX idx_exp_user_date (user_id, purchase_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;