-- V29: Shared family laundry tasks
-- Hierarchy: CR10 -> UC05 -> T01 (B29-DB-ST01)

CREATE TABLE family_laundry_tasks (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  family_group_id BIGINT NOT NULL,
  assigned_user_id BIGINT NULL,
  task_title VARCHAR(150) NOT NULL,
  item_count INT NOT NULL DEFAULT 1,
  task_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
  due_date DATE NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_fam_laund_group FOREIGN KEY (family_group_id) REFERENCES family_groups(id) ON DELETE CASCADE,
  CONSTRAINT fk_fam_laund_user FOREIGN KEY (assigned_user_id) REFERENCES users(id) ON DELETE SET NULL,
  INDEX idx_fam_laund_status (family_group_id, task_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;