package com.kaapstyle.family.controller;

import com.kaapstyle.family.model.FamilyLaundryTask;
import com.kaapstyle.family.repository.FamilyLaundryTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/family/laundry")
public class FamilyLaundryTaskController {

    private final FamilyLaundryTaskRepository laundryRepo;

    @Autowired
    public FamilyLaundryTaskController(FamilyLaundryTaskRepository laundryRepo) {
        this.laundryRepo = laundryRepo;
    }

    @PostMapping
    public ResponseEntity<FamilyLaundryTask> createTask(@RequestBody FamilyLaundryTask task) {
        return ResponseEntity.ok(laundryRepo.save(task));
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<FamilyLaundryTask>> getGroupTasks(@PathVariable Long groupId) {
        return ResponseEntity.ok(laundryRepo.findByFamilyGroupId(groupId));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<FamilyLaundryTask> markCompleted(@PathVariable Long id) {
        return laundryRepo.findById(id).map(t -> {
            t.setTaskStatus("COMPLETED");
            return ResponseEntity.ok(laundryRepo.save(t));
        }).orElse(ResponseEntity.notFound().build());
    }
}
