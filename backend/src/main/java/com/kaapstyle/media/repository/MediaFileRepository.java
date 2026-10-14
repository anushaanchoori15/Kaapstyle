package com.kaapstyle.media.repository;

import com.kaapstyle.media.model.MediaFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link MediaFile}.
 */
@Repository
public interface MediaFileRepository extends JpaRepository<MediaFile, Long> {

    List<MediaFile> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<MediaFile> findByS3Key(String s3Key);
}