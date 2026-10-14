package com.kaapstyle.media.service;

import com.kaapstyle.media.entity.UploadQueueTask;
import com.kaapstyle.media.repository.MediaFileRepository;
import com.kaapstyle.media.repository.UploadQueueTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service managing the lifecycle of client upload queue tasks
 * (QUEUED -> UPLOADING -> COMPLETED / FAILED, with retry support).
 */
@Service
public class UploadQueueService {

    public static final String STATUS_QUEUED = "QUEUED";
    public static final String STATUS_UPLOADING = "UPLOADING";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_FAILED = "FAILED";

    static final int MAX_RETRIES = 3;

    private final UploadQueueTaskRepository uploadQueueTaskRepository;
    private final MediaFileRepository mediaFileRepository;

    @Autowired
    public UploadQueueService(UploadQueueTaskRepository uploadQueueTaskRepository,
                              MediaFileRepository mediaFileRepository) {
        this.uploadQueueTaskRepository = uploadQueueTaskRepository;
        this.mediaFileRepository = mediaFileRepository;
    }

    @Transactional
    public UploadQueueTask registerTask(Long userId, String clientTaskId, String fileName, long totalBytes) {
        UploadQueueTask task = new UploadQueueTask();
        task.setUserId(userId);
        task.setClientTaskId(clientTaskId);
        task.setFileName(fileName);
        task.setTotalBytes(totalBytes);
        task.setBytesUploaded(0L);
        task.setRetryCount(0);
        task.setStatus(STATUS_QUEUED);
        return uploadQueueTaskRepository.save(task);
    }

    @Transactional
    public UploadQueueTask updateProgress(String clientTaskId, long bytesUploaded) {
        UploadQueueTask task = findTask(clientTaskId);
        task.setBytesUploaded(bytesUploaded);
        task.setStatus(STATUS_UPLOADING);
        return uploadQueueTaskRepository.save(task);
    }

    @Transactional
    public UploadQueueTask completeTask(String clientTaskId, Long mediaId) {
        UploadQueueTask task = findTask(clientTaskId);
        if (mediaId != null && !mediaFileRepository.existsById(mediaId)) {
            throw new IllegalArgumentException("Media file not found: " + mediaId);
        }
        task.setStatus(STATUS_COMPLETED);
        task.setMediaId(mediaId);
        task.setBytesUploaded(task.getTotalBytes());
        return uploadQueueTaskRepository.save(task);
    }

    @Transactional
    public UploadQueueTask failTask(String clientTaskId, String errorMessage) {
        UploadQueueTask task = findTask(clientTaskId);
        Integer current = task.getRetryCount();
        int retryCount = (current == null ? 0 : current) + 1;
        task.setRetryCount(retryCount);
        task.setErrorMessage(errorMessage);
        if (retryCount >= MAX_RETRIES) {
            task.setStatus(STATUS_FAILED);
        } else {
            task.setStatus(STATUS_QUEUED);
        }
        return uploadQueueTaskRepository.save(task);
    }

    @Transactional(readOnly = true)
    public List<UploadQueueTask> getTasksForUser(Long userId) {
        return uploadQueueTaskRepository.findByUserId(userId);
    }

    private UploadQueueTask findTask(String clientTaskId) {
        return uploadQueueTaskRepository.findByClientTaskId(clientTaskId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Upload task not found: " + clientTaskId));
    }
}