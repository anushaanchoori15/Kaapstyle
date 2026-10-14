package com.kaapstyle.budget.repository;

import com.kaapstyle.budget.model.ItemCpwSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemCpwSummaryRepository extends JpaRepository<ItemCpwSummary, Long> {
    List<ItemCpwSummary> findByUserIdOrderByCurrentCpwAsc(Long userId);
    List<ItemCpwSummary> findByUserIdAndValueScoreRating(Long userId, String valueScoreRating);
}
