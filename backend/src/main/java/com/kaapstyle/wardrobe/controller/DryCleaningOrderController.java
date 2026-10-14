package com.kaapstyle.wardrobe.controller;

import com.kaapstyle.wardrobe.model.DryCleaningOrder;
import com.kaapstyle.wardrobe.repository.DryCleaningOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/wardrobe/dry-cleaning")
public class DryCleaningOrderController {

    private final DryCleaningOrderRepository orderRepo;

    @Autowired
    public DryCleaningOrderController(DryCleaningOrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }

    @PostMapping
    public ResponseEntity<DryCleaningOrder> createOrder(@RequestBody DryCleaningOrder order) {
        if (order.getDropoffDate() == null) {
            order.setDropoffDate(LocalDate.now());
        }
        return ResponseEntity.ok(orderRepo.save(order));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<DryCleaningOrder>> getUserOrders(
            @PathVariable Long userId,
            @RequestParam(required = false) String status) {
        if (status != null) {
            return ResponseEntity.ok(orderRepo.findByUserIdAndOrderStatus(userId, status));
        }
        return ResponseEntity.ok(orderRepo.findByUserIdOrderByDropoffDateDesc(userId));
    }

    @PutMapping("/{id}/pickup")
    public ResponseEntity<DryCleaningOrder> markPickedUp(@PathVariable Long id) {
        return orderRepo.findById(id).map(order -> {
            order.setOrderStatus("COMPLETED");
            order.setActualPickupDate(LocalDate.now());
            return ResponseEntity.ok(orderRepo.save(order));
        }).orElse(ResponseEntity.notFound().build());
    }
}
