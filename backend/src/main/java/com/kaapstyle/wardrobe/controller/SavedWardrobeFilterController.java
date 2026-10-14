package com.kaapstyle.wardrobe.controller;

import com.kaapstyle.wardrobe.model.SavedWardrobeFilter;
import com.kaapstyle.wardrobe.repository.SavedWardrobeFilterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wardrobe/filters")
public class SavedWardrobeFilterController {

    private final SavedWardrobeFilterRepository filterRepository;

    @Autowired
    public SavedWardrobeFilterController(SavedWardrobeFilterRepository filterRepository) {
        this.filterRepository = filterRepository;
    }

    @PostMapping
    public ResponseEntity<SavedWardrobeFilter> saveFilter(@RequestBody SavedWardrobeFilter filter) {
        return ResponseEntity.ok(filterRepository.save(filter));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SavedWardrobeFilter>> getUserSavedFilters(@PathVariable Long userId) {
        return ResponseEntity.ok(filterRepository.findByUserId(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFilter(@PathVariable Long id) {
        if (filterRepository.existsById(id)) {
            filterRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
