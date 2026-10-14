package com.kaapstyle.family.controller;

import com.kaapstyle.family.model.FamilyGroup;
import com.kaapstyle.family.repository.FamilyGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/family/groups")
public class FamilyGroupController {

    private final FamilyGroupRepository groupRepo;

    @Autowired
    public FamilyGroupController(FamilyGroupRepository groupRepo) {
        this.groupRepo = groupRepo;
    }

    @PostMapping
    public ResponseEntity<FamilyGroup> createGroup(@RequestBody FamilyGroup group) {
        return ResponseEntity.ok(groupRepo.save(group));
    }

    @GetMapping("/owner/{ownerUserId}")
    public ResponseEntity<List<FamilyGroup>> getGroupsByOwner(@PathVariable Long ownerUserId) {
        return ResponseEntity.ok(groupRepo.findByOwnerUserId(ownerUserId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FamilyGroup> getGroupById(@PathVariable Long id) {
        return groupRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
