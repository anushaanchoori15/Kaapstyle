package com.kaapstyle.profile.repository;

import com.kaapstyle.profile.model.SecurityAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SecurityAuditLogRepository extends JpaRepository<SecurityAuditLog, Long> {

    List<SecurityAuditLog> findByUserIdOrderByCreatedAtDesc(Long userId);
}