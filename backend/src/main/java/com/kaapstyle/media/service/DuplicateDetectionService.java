package com.kaapstyle.media.service;

import com.kaapstyle.media.model.DuplicateCandidatePair;
import com.kaapstyle.media.repository.DuplicateCandidatePairRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DuplicateDetectionService {

    private final DuplicateCandidatePairRepository duplicateRepo;

    @Autowired
    public DuplicateDetectionService(DuplicateCandidatePairRepository duplicateRepo) {
        this.duplicateRepo = duplicateRepo;
    }

    @Transactional
    public DuplicateCandidatePair registerPotentialDuplicate(Long userId, Long originalMediaId, Long candidateMediaId, BigDecimal similarityScore) {
        DuplicateCandidatePair pair = new DuplicateCandidatePair(userId, originalMediaId, candidateMediaId, similarityScore);
        return duplicateRepo.save(pair);
    }

    @Transactional(readOnly = true)
    public List<DuplicateCandidatePair> getPendingDuplicates(Long userId) {
        return duplicateRepo.findByUserIdAndResolutionStatus(userId, "PENDING");
    }

    @Transactional
    public DuplicateCandidatePair resolveDuplicate(Long pairId, String resolution) {
        DuplicateCandidatePair pair = duplicateRepo.findById(pairId)
                .orElseThrow(() -> new IllegalArgumentException("Duplicate pair not found: " + pairId));
        pair.setResolutionStatus(resolution);
        pair.setResolvedAt(LocalDateTime.now());
        return duplicateRepo.save(pair);
    }
}
