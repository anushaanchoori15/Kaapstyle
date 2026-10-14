package com.kaapstyle.wardrobe.controller;

import com.kaapstyle.wardrobe.model.BodyMeasurement;
import com.kaapstyle.wardrobe.repository.BodyMeasurementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/wardrobe/measurements")
public class BodyMeasurementController {

    private final BodyMeasurementRepository measurementRepo;

    @Autowired
    public BodyMeasurementController(BodyMeasurementRepository measurementRepo) {
        this.measurementRepo = measurementRepo;
    }

    @PostMapping
    public ResponseEntity<BodyMeasurement> saveMeasurement(@RequestBody BodyMeasurement measurement) {
        if (measurement.getRecordedAt() == null) {
            measurement.setRecordedAt(LocalDate.now());
        }
        return ResponseEntity.ok(measurementRepo.save(measurement));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BodyMeasurement>> getUserMeasurements(
            @PathVariable Long userId,
            @RequestParam(required = false) String profileTag) {
        if (profileTag != null) {
            return ResponseEntity.ok(measurementRepo.findByUserIdAndProfileTagOrderByRecordedAtDesc(userId, profileTag));
        }
        return ResponseEntity.ok(measurementRepo.findByUserIdOrderByRecordedAtDesc(userId));
    }
}
