-- Flyway migration V25: packing lists
-- Hierarchy: CR08 -> UC01 -> T01 (B25-DB-ST01)
-- Target: MySQL 8.x, InnoDB, utf8mb4

CREATE TABLE travel_trips (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  destination VARCHAR(150) NOT NULL,
  start_date DATE NOT NULL,
  end_date DATE NOT NULL,
  trip_type VARCHAR(50) NOT NULL DEFAULT 'LEISURE',
  weather_expected VARCHAR(100) NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_trip_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_trip_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE packing_list_items (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  trip_id BIGINT NOT NULL,
  item_id BIGINT NULL,
  custom_name VARCHAR(150) NOT NULL,
  quantity INT NOT NULL DEFAULT 1,
  is_packed BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT fk_pack_trip FOREIGN KEY (trip_id) REFERENCES travel_trips(id) ON DELETE CASCADE,
  CONSTRAINT fk_pack_item FOREIGN KEY (item_id) REFERENCES wardrobe_items(id) ON DELETE SET NULL,
  INDEX idx_pack_trip (trip_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;