package com.kaapstyle.media.repository;

import com.kaapstyle.media.model.MediaSanitizationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MediaSanitizationLogRepository extends JpaRepository<MediaSanitizationLog, Long> {
    Optional<MediaSanitizationLog> findByMediaId(Long mediaId);
}
