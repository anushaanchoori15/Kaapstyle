package com.kaapstyle.outfit.controller;

import com.kaapstyle.outfit.model.ItemWearHistory;
import com.kaapstyle.outfit.repository.ItemWearHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/outfits/wear-history")
public class ItemWearHistoryController {

    private final ItemWearHistoryRepository wearRepo;

    @Autowired
    public ItemWearHistoryController(ItemWearHistoryRepository wearRepo) {
        this.wearRepo = wearRepo;
    }

    @PostMapping
    public ResponseEntity<ItemWearHistory> logWear(@RequestBody ItemWearHistory history) {
        if (history.getWornDate() == null) {
            history.setWornDate(LocalDate.now());
        }
        return ResponseEntity.ok(wearRepo.save(history));
    }

    @GetMapping("/item/{itemId}")
    public ResponseEntity<List<ItemWearHistory>> getItemHistory(
            @PathVariable Long itemId,
            @RequestParam Long userId) {
        return ResponseEntity.ok(wearRepo.findByUserIdAndItemIdOrderByWornDateDesc(userId, itemId));
    }
}
