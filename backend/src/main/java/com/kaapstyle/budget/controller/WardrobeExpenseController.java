package com.kaapstyle.budget.controller;

import com.kaapstyle.budget.model.WardrobeExpense;
import com.kaapstyle.budget.repository.WardrobeExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/budget/expenses")
public class WardrobeExpenseController {

    private final WardrobeExpenseRepository expenseRepo;

    @Autowired
    public WardrobeExpenseController(WardrobeExpenseRepository expenseRepo) {
        this.expenseRepo = expenseRepo;
    }

    @PostMapping
    public ResponseEntity<WardrobeExpense> logExpense(@RequestBody WardrobeExpense expense) {
        if (expense.getPurchaseDate() == null) {
            expense.setPurchaseDate(LocalDate.now());
        }
        return ResponseEntity.ok(expenseRepo.save(expense));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<WardrobeExpense>> getUserExpenses(@PathVariable Long userId) {
        return ResponseEntity.ok(expenseRepo.findByUserIdOrderByPurchaseDateDesc(userId));
    }
}
