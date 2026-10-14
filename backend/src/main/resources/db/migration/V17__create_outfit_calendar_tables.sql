-- V17: Outfit calendar plans
-- Traceability: CR03 -> UC09 -> T01 (B17-DB-ST01)

CREATE TABLE outfit_calendar_plans (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  outfit_id BIGINT NOT NULL,
  plan_date DATE NOT NULL,
  meal_or_time_slot VARCHAR(30) NOT NULL DEFAULT 'DAY',
  is_worn BOOLEAN NOT NULL DEFAULT FALSE,
  weather_forecast_summary VARCHAR(100) NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_cal_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_cal_outfit FOREIGN KEY (outfit_id) REFERENCES outfits(id) ON DELETE CASCADE,
  UNIQUE KEY uq_user_date_slot (user_id, plan_date, meal_or_time_slot),
  INDEX idx_cal_user_date (user_id, plan_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;