package com.kaapstyle.outfit.repository;

import com.kaapstyle.outfit.model.OutfitCalendarPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OutfitCalendarPlanRepository extends JpaRepository<OutfitCalendarPlan, Long> {
    List<OutfitCalendarPlan> findByUserIdAndPlanDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
    List<OutfitCalendarPlan> findByUserIdAndPlanDate(Long userId, LocalDate planDate);
}
