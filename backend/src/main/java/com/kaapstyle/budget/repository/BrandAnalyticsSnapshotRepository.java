package com.kaapstyle.budget.repository;

import com.kaapstyle.budget.model.BrandAnalyticsSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandAnalyticsSnapshotRepository extends JpaRepository<BrandAnalyticsSnapshot, Long> {
    List<BrandAnalyticsSnapshot> findByUserIdOrderByTotalSpendDesc(Long userId);
}
