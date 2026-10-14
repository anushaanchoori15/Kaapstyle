package com.kaapstyle.outfit.controller;

import com.kaapstyle.outfit.model.Outfit;
import com.kaapstyle.outfit.repository.OutfitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/outfits")
public class OutfitController {

    private final OutfitRepository outfitRepository;

    @Autowired
    public OutfitController(OutfitRepository outfitRepository) {
        this.outfitRepository = outfitRepository;
    }

    @PostMapping
    public ResponseEntity<Outfit> createOutfit(@RequestBody Outfit outfit) {
        return ResponseEntity.ok(outfitRepository.save(outfit));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Outfit>> getUserOutfits(
            @PathVariable Long userId,
            @RequestParam(required = false) String occasion) {
        if (occasion != null) {
            return ResponseEntity.ok(outfitRepository.findByUserIdAndOccasion(userId, occasion));
        }
        return ResponseEntity.ok(outfitRepository.findByUserId(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Outfit> getOutfitById(@PathVariable Long id) {
        return outfitRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/favorite")
    public ResponseEntity<Outfit> toggleFavorite(@PathVariable Long id) {
        return outfitRepository.findById(id).map(outfit -> {
            outfit.setIsFavorite(!Boolean.TRUE.equals(outfit.getIsFavorite()));
            return ResponseEntity.ok(outfitRepository.save(outfit));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOutfit(@PathVariable Long id) {
        if (outfitRepository.existsById(id)) {
            outfitRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
