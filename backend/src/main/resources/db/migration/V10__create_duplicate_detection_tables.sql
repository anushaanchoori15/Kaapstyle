-- V10: Duplicate detection tables
-- Hierarchy: CR01 -> UC13 -> T01 (B10-DB-ST01)

CREATE TABLE duplicate_candidate_pairs (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  original_media_id BIGINT NOT NULL,
  candidate_media_id BIGINT NOT NULL,
  similarity_score DECIMAL(5,4) NOT NULL,
  resolution_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  resolved_at TIMESTAMP NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_dup_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_dup_orig FOREIGN KEY (original_media_id) REFERENCES media_files(id) ON DELETE CASCADE,
  CONSTRAINT fk_dup_cand FOREIGN KEY (candidate_media_id) REFERENCES media_files(id) ON DELETE CASCADE,
  INDEX idx_dup_user_status (user_id, resolution_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;