package com.kaapstyle.kids.controller;

import com.kaapstyle.kids.model.KidsGrowthLog;
import com.kaapstyle.kids.repository.KidsGrowthLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/kids/growth")
public class KidsGrowthLogController {

    private final KidsGrowthLogRepository growthRepo;

    @Autowired
    public KidsGrowthLogController(KidsGrowthLogRepository growthRepo) {
        this.growthRepo = growthRepo;
    }

    @PostMapping
    public ResponseEntity<KidsGrowthLog> recordGrowth(@RequestBody KidsGrowthLog log) {
        if (log.getRecordedAt() == null) {
            log.setRecordedAt(LocalDate.now());
        }
        return ResponseEntity.ok(growthRepo.save(log));
    }

    @GetMapping("/kid/{kidId}")
    public ResponseEntity<List<KidsGrowthLog>> getKidGrowthHistory(@PathVariable Long kidId) {
        return ResponseEntity.ok(growthRepo.findByKidIdOrderByRecordedAtDesc(kidId));
    }
}
