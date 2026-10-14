package com.kaapstyle.media.controller;

import com.kaapstyle.media.model.MediaFile;
import com.kaapstyle.media.service.MediaStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * REST controller for media upload workflow (presign, confirm) and retrieval.
 *
 * Assumptions about MediaStorageService (see MediaStorageService contract):
 *  - Object generatePresignedUpload(String userId, String fileName, String mimeType, long fileSizeBytes)
 *  - MediaFile confirmUpload(String mediaId, String sha256Hash)
 *  - List&lt;MediaFile&gt; getUserMedia(String userId)
 */
@RestController
@RequestMapping("/api/media")
@CrossOrigin
public class MediaController {

    @Autowired
    private MediaStorageService mediaStorageService;

    @PostMapping("/presign")
    public ResponseEntity<Object> presign(@RequestBody Map<String, Object> body) {
        String userId = requireString(body, "userId");
        String fileName = requireString(body, "fileName");
        String mimeType = requireString(body, "mimeType");
        long fileSizeBytes = requireLong(body, "fileSizeBytes");

        Object presign = mediaStorageService.generatePresignedUpload(userId, fileName, mimeType, fileSizeBytes);
        return ResponseEntity.ok(presign);
    }

    @PostMapping("/confirm")
    public ResponseEntity<MediaFile> confirm(@RequestBody Map<String, Object> body) {
        String mediaId = requireString(body, "mediaId");
        String sha256Hash = requireString(body, "sha256Hash");

        MediaFile confirmed = mediaStorageService.confirmUpload(mediaId, sha256Hash);
        return ResponseEntity.ok(confirmed);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MediaFile>> getUserMedia(@PathVariable("userId") String userId) {
        return ResponseEntity.ok(mediaStorageService.getUserMedia(userId));
    }

    // ---------------------------------------------------------------------
    // Exception handling
    // ---------------------------------------------------------------------

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, Object>> handleNoSuchElement(NoSuchElementException ex) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // ---------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------

    private ResponseEntity<Map<String, Object>> buildError(HttpStatus status, String message) {
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("status", status.value());
        error.put("error", status.getReasonPhrase());
        error.put("message", message);
        return ResponseEntity.status(status).body(error);
    }

    private String requireString(Map<String, Object> body, String key) {
        if (body == null) {
            throw new IllegalArgumentException("Request body is required");
        }
        Object value = body.get(key);
        if (value == null || value.toString().trim().isEmpty()) {
            throw new IllegalArgumentException("Missing required field: " + key);
        }
        return value.toString().trim();
    }

    private long requireLong(Map<String, Object> body, String key) {
        if (body == null) {
            throw new IllegalArgumentException("Request body is required");
        }
        Object value = body.get(key);
        if (value == null) {
            throw new IllegalArgumentException("Missing required field: " + key);
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return Long.parseLong(value.toString().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Field '" + key + "' must be a valid number");
        }
    }
}