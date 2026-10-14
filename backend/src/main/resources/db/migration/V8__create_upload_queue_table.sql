-- V8: Create upload_queue_tasks table
-- Hierarchy: CR01 -> UC05 -> T01 (B08-DB-ST01)
-- Engine/charset: InnoDB, utf8mb4 / utf8mb4_unicode_ci

CREATE TABLE upload_queue_tasks (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  client_task_id VARCHAR(100) NOT NULL UNIQUE,
  media_id BIGINT NULL,
  file_name VARCHAR(255) NOT NULL,
  bytes_uploaded BIGINT NOT NULL DEFAULT 0,
  total_bytes BIGINT NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'QUEUED',
  retry_count INT NOT NULL DEFAULT 0,
  error_message TEXT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_queue_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_queue_media FOREIGN KEY (media_id) REFERENCES media_files(id) ON DELETE SET NULL,
  INDEX idx_queue_user_status (user_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;