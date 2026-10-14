package com.kaapstyle.wardrobe.controller;

import com.kaapstyle.wardrobe.model.CapsuleWardrobe;
import com.kaapstyle.wardrobe.repository.CapsuleWardrobeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wardrobe/capsules")
public class CapsuleWardrobeController {

    private final CapsuleWardrobeRepository capsuleRepository;

    @Autowired
    public CapsuleWardrobeController(CapsuleWardrobeRepository capsuleRepository) {
        this.capsuleRepository = capsuleRepository;
    }

    @PostMapping
    public ResponseEntity<CapsuleWardrobe> createCapsule(@RequestBody CapsuleWardrobe capsule) {
        return ResponseEntity.ok(capsuleRepository.save(capsule));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CapsuleWardrobe>> getUserCapsules(@PathVariable Long userId) {
        return ResponseEntity.ok(capsuleRepository.findByUserId(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CapsuleWardrobe> getCapsuleById(@PathVariable Long id) {
        return capsuleRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCapsule(@PathVariable Long id) {
        if (capsuleRepository.existsById(id)) {
            capsuleRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
