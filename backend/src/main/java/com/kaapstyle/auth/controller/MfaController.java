package com.kaapstyle.auth.controller;

import com.kaapstyle.auth.service.MfaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST controller exposing MFA (TOTP) setup, verification and refresh-token rotation.
 *
 * Assumptions about MfaService (constructor-injected):
 *  - Map<String, String> generateTotpSecret(Long userId)  -> keys: secretKey, otpauthUri
 *  - boolean verifyTotpCode(Long userId, String code)
 *  - Map<String, String> rotateRefreshToken(Long userId, String refreshToken) -> keys: accessToken, refreshToken
 */
@RestController
@RequestMapping("/api/auth/mfa")
@CrossOrigin
public class MfaController {

    private static final String SIX_DIGITS = "\\d{6}";

    private final MfaService mfaService;

    public MfaController(MfaService mfaService) {
        this.mfaService = mfaService;
    }

    @PostMapping("/setup/{userId}")
    public ResponseEntity<Map<String, String>> setup(@PathVariable("userId") Long userId) {
        Map<String, String> result = mfaService.generateTotpSecret(userId);
        return ResponseEntity.ok(Map.of(
                "secretKey", result.get("secretKey"),
                "otpauthUri", result.get("otpauthUri")));
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verify(@RequestBody Map<String, Object> body) {
        Long userId = parseUserId(body == null ? null : body.get("userId"));
        Object codeObj = body == null ? null : body.get("code");
        String code = codeObj == null ? null : String.valueOf(codeObj).trim();

        if (userId == null || code == null || !code.matches(SIX_DIGITS)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("verified", false));
        }

        boolean verified = mfaService.verifyTotpCode(userId, code);
        if (!verified) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("verified", false));
        }
        return ResponseEntity.ok(Map.of("verified", true));
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(@RequestBody Map<String, Object> body) {
        Long userId = parseUserId(body == null ? null : body.get("userId"));
        Object tokenObj = body == null ? null : body.get("refreshToken");
        String refreshToken = tokenObj == null ? null : String.valueOf(tokenObj);

        if (userId == null || refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Map<String, String> tokens = mfaService.rotateRefreshToken(userId, refreshToken);
        return ResponseEntity.ok(Map.of(
                "accessToken", tokens.get("accessToken"),
                "refreshToken", tokens.get("refreshToken")));
    }

    private Long parseUserId(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        try {
            return Long.valueOf(String.valueOf(value).trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}