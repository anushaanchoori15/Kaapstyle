package com.kaapstyle.account.controller;

import com.kaapstyle.account.model.GdprExportRequest;
import com.kaapstyle.account.service.AccountLifecycleService;
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
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * REST controller exposing account lifecycle operations:
 * account deletion (request / cancel) and GDPR data export (request / status).
 *
 * Business logic lives exclusively in {@link AccountLifecycleService}.
 */
@RestController
@RequestMapping("/api/account")
@CrossOrigin
public class AccountLifecycleController {

    private final AccountLifecycleService accountLifecycleService;

    @Autowired
    public AccountLifecycleController(AccountLifecycleService accountLifecycleService) {
        this.accountLifecycleService = accountLifecycleService;
    }

    /**
     * Requests account deletion. Requires the user's current password.
     * Responds with the timestamp at which deletion is scheduled.
     */
    @PostMapping("/{userId}/delete")
    public ResponseEntity<Map<String, Object>> requestAccountDeletion(
            @PathVariable Long userId,
            @RequestBody Map<String, String> body) {

        String password = body == null ? null : body.get("password");
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password is required.");
        }

        var deletionScheduledAt = accountLifecycleService.requestAccountDeletion(userId, password);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("deletionScheduledAt", deletionScheduledAt);
        return ResponseEntity.ok(response);
    }

    /**
     * Cancels a pending account deletion.
     */
    @PostMapping("/{userId}/cancel-deletion")
    public ResponseEntity<Map<String, String>> cancelAccountDeletion(@PathVariable Long userId) {
        accountLifecycleService.cancelAccountDeletion(userId);
        return ResponseEntity.ok(Map.of("message", "Deletion cancelled."));
    }

    /**
     * Requests a new GDPR data export for the user.
     */
    @PostMapping("/{userId}/gdpr-export")
    public ResponseEntity<GdprExportRequest> requestGdprExport(@PathVariable Long userId) {
        GdprExportRequest exportRequest = accountLifecycleService.requestGdprExport(userId);
        return ResponseEntity.ok(exportRequest);
    }

    /**
     * Returns the latest GDPR export request for the user.
     */
    @GetMapping("/{userId}/gdpr-export/status")
    public ResponseEntity<GdprExportRequest> getExportStatus(@PathVariable Long userId) {
        GdprExportRequest exportRequest = accountLifecycleService.getExportStatus(userId);
        return ResponseEntity.ok(exportRequest);
    }

    // ------------------------------------------------------------------
    // Exception handling
    // ------------------------------------------------------------------

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatus(ResponseStatusException ex) {
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        String message = ex.getReason() != null ? ex.getReason() : status.getReasonPhrase();
        return buildError(status, message);
    }

    private ResponseEntity<Map<String, Object>> buildError(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}