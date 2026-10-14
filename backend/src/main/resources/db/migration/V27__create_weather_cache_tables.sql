-- Flyway migration V27: create user weather cache table
-- Hierarchy: CR09 -> UC01 -> T01
-- Story ref: B27-DB-ST01
-- Target: MySQL 8.x, InnoDB, utf8mb4

CREATE TABLE user_weather_caches (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  city_name VARCHAR(100) NOT NULL,
  country_code VARCHAR(10) NULL,
  temperature_celsius DECIMAL(4,1) NOT NULL,
  weather_condition VARCHAR(50) NOT NULL,
  precipitation_chance INT NOT NULL DEFAULT 0,
  humidity_percent INT NOT NULL DEFAULT 50,
  fetched_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_weath_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_weath_user_city (user_id, city_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;