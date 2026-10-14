package com.kaapstyle.budget.repository;

import com.kaapstyle.budget.model.WardrobeExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WardrobeExpenseRepository extends JpaRepository<WardrobeExpense, Long> {
    List<WardrobeExpense> findByUserIdOrderByPurchaseDateDesc(Long userId);
    List<WardrobeExpense> findByUserIdAndPurchaseDateBetween(Long userId, LocalDate start, LocalDate end);
}
