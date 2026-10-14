package com.kaapstyle.travel.controller;

import com.kaapstyle.travel.model.LuggageContainer;
import com.kaapstyle.travel.repository.LuggageContainerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/travel/luggage")
public class LuggageContainerController {

    private final LuggageContainerRepository luggageRepo;

    @Autowired
    public LuggageContainerController(LuggageContainerRepository luggageRepo) {
        this.luggageRepo = luggageRepo;
    }

    @PostMapping
    public ResponseEntity<LuggageContainer> createContainer(@RequestBody LuggageContainer container) {
        return ResponseEntity.ok(luggageRepo.save(container));
    }

    @GetMapping("/trip/{tripId}")
    public ResponseEntity<List<LuggageContainer>> getTripContainers(@PathVariable Long tripId) {
        return ResponseEntity.ok(luggageRepo.findByTripId(tripId));
    }

    @PutMapping("/{id}/weight")
    public ResponseEntity<LuggageContainer> updateWeight(
            @PathVariable Long id,
            @RequestParam BigDecimal weightKg) {
        return luggageRepo.findById(id).map(c -> {
            c.setCurrentWeightKg(weightKg);
            return ResponseEntity.ok(luggageRepo.save(c));
        }).orElse(ResponseEntity.notFound().build());
    }
}
