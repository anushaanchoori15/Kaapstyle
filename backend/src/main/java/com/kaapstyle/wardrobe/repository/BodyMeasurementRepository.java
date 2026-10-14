package com.kaapstyle.wardrobe.repository;

import com.kaapstyle.wardrobe.model.BodyMeasurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BodyMeasurementRepository extends JpaRepository<BodyMeasurement, Long> {
    List<BodyMeasurement> findByUserIdOrderByRecordedAtDesc(Long userId);
    List<BodyMeasurement> findByUserIdAndProfileTagOrderByRecordedAtDesc(Long userId, String profileTag);
}
