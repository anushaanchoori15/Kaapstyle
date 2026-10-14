package com.kaapstyle.outfit.repository;

import com.kaapstyle.outfit.model.ItemWearHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ItemWearHistoryRepository extends JpaRepository<ItemWearHistory, Long> {
    List<ItemWearHistory> findByUserIdAndItemIdOrderByWornDateDesc(Long userId, Long itemId);
    List<ItemWearHistory> findByUserIdAndWornDateBetween(Long userId, LocalDate start, LocalDate end);
}
