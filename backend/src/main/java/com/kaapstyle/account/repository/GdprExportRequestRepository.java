package com.kaapstyle.account.repository;

import com.kaapstyle.account.model.GdprExportRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GdprExportRequestRepository extends JpaRepository<GdprExportRequest, Long> {

    List<GdprExportRequest> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<GdprExportRequest> findTopByUserIdAndStatusOrderByCreatedAtDesc(Long userId, String status);
}