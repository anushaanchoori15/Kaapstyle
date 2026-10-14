package com.kaapstyle.media.repository;

import com.kaapstyle.media.model.UploadQueueTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UploadQueueTaskRepository extends JpaRepository<UploadQueueTask, Long> {

    Optional<UploadQueueTask> findByClientTaskId(String clientTaskId);

    List<UploadQueueTask> findByUserIdAndStatus(Long userId, String status);
}