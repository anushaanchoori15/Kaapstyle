-- Flyway migration V26: luggage_containers table
-- Traceability: CR08 -> UC05 -> T01 (B26-DB-ST01)
-- Requires travel_trips(id) to exist with a BIGINT primary key (earlier migration).

CREATE TABLE luggage_containers (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  trip_id BIGINT NOT NULL,
  container_name VARCHAR(100) NOT NULL,
  container_type VARCHAR(50) NOT NULL DEFAULT 'CHECK_IN',
  max_weight_limit_kg DECIMAL(5,2) NOT NULL DEFAULT 23.00,
  current_weight_kg DECIMAL(5,2) NOT NULL DEFAULT 0.00,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_lug_trip FOREIGN KEY (trip_id) REFERENCES travel_trips(id) ON DELETE CASCADE,
  INDEX idx_lug_trip (trip_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;