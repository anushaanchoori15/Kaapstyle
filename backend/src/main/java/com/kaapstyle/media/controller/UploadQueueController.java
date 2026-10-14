package com.kaapstyle.media.controller;

import com.kaapstyle.media.model.UploadQueueTask;
import com.kaapstyle.media.service.UploadQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/media/queue")
public class UploadQueueController {

    private final UploadQueueService uploadQueueService;

    @Autowired
    public UploadQueueController(UploadQueueService uploadQueueService) {
        this.uploadQueueService = uploadQueueService;
    }

    @PostMapping("/enqueue")
    public ResponseEntity<UploadQueueTask> enqueueUpload(
            @RequestParam Long userId,
            @RequestParam String originalFilename,
            @RequestParam String mimeType,
            @RequestParam Long totalBytes,
            @RequestParam(required = false, defaultValue = "NORMAL") String priorityLevel) {
        UploadQueueTask task = uploadQueueService.enqueueUpload(userId, originalFilename, mimeType, totalBytes, priorityLevel);
        return ResponseEntity.ok(task);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UploadQueueTask>> getUserQueue(@PathVariable Long userId) {
        return ResponseEntity.ok(uploadQueueService.getUserQueue(userId));
    }

    @PostMapping("/{taskId}/progress")
    public ResponseEntity<UploadQueueTask> updateProgress(
            @PathVariable Long taskId,
            @RequestBody Map<String, Long> payload) {
        Long bytesUploaded = payload.getOrDefault("bytesUploaded", 0L);
        UploadQueueTask task = uploadQueueService.updateProgress(taskId, bytesUploaded);
        return ResponseEntity.ok(task);
    }

    @PostMapping("/{taskId}/complete")
    public ResponseEntity<UploadQueueTask> markCompleted(
            @PathVariable Long taskId,
            @RequestBody Map<String, String> payload) {
        String storageKey = payload.get("storageKey");
        UploadQueueTask task = uploadQueueService.markCompleted(taskId, storageKey);
        return ResponseEntity.ok(task);
    }

    @PostMapping("/{taskId}/fail")
    public ResponseEntity<UploadQueueTask> markFailed(
            @PathVariable Long taskId,
            @RequestBody Map<String, String> payload) {
        String errorMessage = payload.getOrDefault("errorMessage", "Unknown upload error");
        UploadQueueTask task = uploadQueueService.markFailed(taskId, errorMessage);
        return ResponseEntity.ok(task);
    }

    @PostMapping("/{taskId}/pause")
    public ResponseEntity<UploadQueueTask> pauseTask(@PathVariable Long taskId) {
        return ResponseEntity.ok(uploadQueueService.pauseTask(taskId));
    }

    @PostMapping("/{taskId}/resume")
    public ResponseEntity<UploadQueueTask> resumeTask(@PathVariable Long taskId) {
        return ResponseEntity.ok(uploadQueueService.resumeTask(taskId));
    }
}
