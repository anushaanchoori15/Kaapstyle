package com.kaapstyle.kids.controller;

import com.kaapstyle.kids.model.KidsProfile;
import com.kaapstyle.kids.repository.KidsProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/kids/profiles")
public class KidsProfileController {

    private final KidsProfileRepository kidsRepo;

    @Autowired
    public KidsProfileController(KidsProfileRepository kidsRepo) {
        this.kidsRepo = kidsRepo;
    }

    @PostMapping
    public ResponseEntity<KidsProfile> createProfile(@RequestBody KidsProfile profile) {
        return ResponseEntity.ok(kidsRepo.save(profile));
    }

    @GetMapping("/guardian/{guardianId}")
    public ResponseEntity<List<KidsProfile>> getProfilesByGuardian(@PathVariable Long guardianId) {
        return ResponseEntity.ok(kidsRepo.findByGuardianId(guardianId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<KidsProfile> getProfileById(@PathVariable Long id) {
        return kidsRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
