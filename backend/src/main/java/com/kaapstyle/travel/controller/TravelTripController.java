package com.kaapstyle.travel.controller;

import com.kaapstyle.travel.model.TravelTrip;
import com.kaapstyle.travel.repository.TravelTripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/travel/trips")
public class TravelTripController {

    private final TravelTripRepository tripRepo;

    @Autowired
    public TravelTripController(TravelTripRepository tripRepo) {
        this.tripRepo = tripRepo;
    }

    @PostMapping
    public ResponseEntity<TravelTrip> createTrip(@RequestBody TravelTrip trip) {
        return ResponseEntity.ok(tripRepo.save(trip));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TravelTrip>> getUserTrips(@PathVariable Long userId) {
        return ResponseEntity.ok(tripRepo.findByUserIdOrderByStartDateAsc(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TravelTrip> getTripById(@PathVariable Long id) {
        return tripRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrip(@PathVariable Long id) {
        if (tripRepo.existsById(id)) {
            tripRepo.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
