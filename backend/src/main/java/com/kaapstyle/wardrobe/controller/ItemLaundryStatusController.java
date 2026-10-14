package com.kaapstyle.wardrobe.controller;

import com.kaapstyle.wardrobe.model.ItemLaundryStatus;
import com.kaapstyle.wardrobe.repository.ItemLaundryStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/wardrobe/laundry")
public class ItemLaundryStatusController {

    private final ItemLaundryStatusRepository laundryRepo;

    @Autowired
    public ItemLaundryStatusController(ItemLaundryStatusRepository laundryRepo) {
        this.laundryRepo = laundryRepo;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ItemLaundryStatus>> getUserLaundry(
            @PathVariable Long userId,
            @RequestParam(required = false, defaultValue = "IN_HAMPER") String status) {
        return ResponseEntity.ok(laundryRepo.findByUserIdAndStatus(userId, status));
    }

    @PutMapping("/item/{itemId}/status")
    public ResponseEntity<ItemLaundryStatus> updateStatus(
            @PathVariable Long itemId,
            @RequestParam String status) {
        return laundryRepo.findById(itemId).map(item -> {
            item.setStatus(status);
            if ("CLEAN".equalsIgnoreCase(status)) {
                item.setWashCycleCount(item.getWashCycleCount() + 1);
                item.setLastWashedDate(LocalDate.now());
            }
            return ResponseEntity.ok(laundryRepo.save(item));
        }).orElse(ResponseEntity.notFound().build());
    }
}
