package com.kaapstyle.kids.repository;

import com.kaapstyle.kids.model.KidsGrowthLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KidsGrowthLogRepository extends JpaRepository<KidsGrowthLog, Long> {
    List<KidsGrowthLog> findByKidIdOrderByRecordedAtDesc(Long kidId);
}
