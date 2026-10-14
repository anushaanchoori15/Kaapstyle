package com.kaapstyle.budget.controller;

import com.kaapstyle.budget.model.BrandAnalyticsSnapshot;
import com.kaapstyle.budget.repository.BrandAnalyticsSnapshotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/budget/brands")
public class BrandAnalyticsController {

    private final BrandAnalyticsSnapshotRepository brandRepo;

    @Autowired
    public BrandAnalyticsController(BrandAnalyticsSnapshotRepository brandRepo) {
        this.brandRepo = brandRepo;
    }

    @PostMapping("/snapshot")
    public ResponseEntity<BrandAnalyticsSnapshot> recordSnapshot(@RequestBody BrandAnalyticsSnapshot snapshot) {
        return ResponseEntity.ok(brandRepo.save(snapshot));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BrandAnalyticsSnapshot>> getUserBrandAnalytics(@PathVariable Long userId) {
        return ResponseEntity.ok(brandRepo.findByUserIdOrderByTotalSpendDesc(userId));
    }
}
