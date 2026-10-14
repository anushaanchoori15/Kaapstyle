package com.kaapstyle.budget.controller;

import com.kaapstyle.budget.model.ItemCpwSummary;
import com.kaapstyle.budget.repository.ItemCpwSummaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@RestController
@RequestMapping("/api/v1/budget/cpw")
public class ItemCpwSummaryController {

    private final ItemCpwSummaryRepository cpwRepo;

    @Autowired
    public ItemCpwSummaryController(ItemCpwSummaryRepository cpwRepo) {
        this.cpwRepo = cpwRepo;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ItemCpwSummary>> getUserCpwList(@PathVariable Long userId) {
        return ResponseEntity.ok(cpwRepo.findByUserIdOrderByCurrentCpwAsc(userId));
    }

    @PostMapping("/calculate/{itemId}")
    public ResponseEntity<ItemCpwSummary> updateCpw(
            @PathVariable Long itemId,
            @RequestParam Long userId,
            @RequestParam BigDecimal purchasePrice,
            @RequestParam Integer wearCount) {
        ItemCpwSummary summary = cpwRepo.findById(itemId).orElse(new ItemCpwSummary());
        summary.setItemId(itemId);
        summary.setUserId(userId);
        summary.setPurchasePrice(purchasePrice);
        summary.setWearCount(wearCount);

        if (wearCount > 0) {
            BigDecimal cpw = purchasePrice.divide(BigDecimal.valueOf(wearCount), 2, RoundingMode.HALF_UP);
            summary.setCurrentCpw(cpw);
            if (cpw.compareTo(new BigDecimal("5.00")) <= 0) {
                summary.setValueScoreRating("GREAT_VALUE");
            } else if (cpw.compareTo(new BigDecimal("20.00")) <= 0) {
                summary.setValueScoreRating("MODERATE_VALUE");
            } else {
                summary.setValueScoreRating("UNDERUTILIZED");
            }
        } else {
            summary.setCurrentCpw(purchasePrice);
            summary.setValueScoreRating("NEW");
        }

        return ResponseEntity.ok(cpwRepo.save(summary));
    }
}
