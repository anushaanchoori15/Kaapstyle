package com.kaapstyle.media.repository;

import com.kaapstyle.media.model.DuplicateCandidatePair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DuplicateCandidatePairRepository extends JpaRepository<DuplicateCandidatePair, Long> {
    List<DuplicateCandidatePair> findByUserIdAndResolutionStatus(Long userId, String resolutionStatus);
}
