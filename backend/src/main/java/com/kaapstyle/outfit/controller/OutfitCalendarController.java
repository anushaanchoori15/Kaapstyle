package com.kaapstyle.outfit.controller;

import com.kaapstyle.outfit.model.OutfitCalendarPlan;
import com.kaapstyle.outfit.repository.OutfitCalendarPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/outfits/calendar")
public class OutfitCalendarController {

    private final OutfitCalendarPlanRepository calendarRepo;

    @Autowired
    public OutfitCalendarController(OutfitCalendarPlanRepository calendarRepo) {
        this.calendarRepo = calendarRepo;
    }

    @PostMapping("/schedule")
    public ResponseEntity<OutfitCalendarPlan> schedulePlan(@RequestBody OutfitCalendarPlan plan) {
        return ResponseEntity.ok(calendarRepo.save(plan));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OutfitCalendarPlan>> getCalendarRange(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(calendarRepo.findByUserIdAndPlanDateBetween(userId, startDate, endDate));
    }

    @PutMapping("/{id}/mark-worn")
    public ResponseEntity<OutfitCalendarPlan> markAsWorn(@PathVariable Long id) {
        return calendarRepo.findById(id).map(plan -> {
            plan.setIsWorn(true);
            return ResponseEntity.ok(calendarRepo.save(plan));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelPlan(@PathVariable Long id) {
        if (calendarRepo.existsById(id)) {
            calendarRepo.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
