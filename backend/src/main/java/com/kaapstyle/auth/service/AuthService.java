package com.kaapstyle.auth.service;

import com.kaapstyle.auth.dto.AuthResponse;
import com.kaapstyle.auth.dto.LoginRequest;
import com.kaapstyle.auth.dto.RegisterRequest;
import com.kaapstyle.auth.model.User;
import com.kaapstyle.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Service
public class AuthService {

    private static final long TOKEN_TTL_SECONDS = 3600L;
    private static final int SALT_BYTES = 16;

    private final SecureRandom secureRandom = new SecureRandom();

    @Autowired
    private UserRepository userRepository;

    @Value("${kaapstyle.auth.jwt-secret:change-me-kaapstyle-default-secret-key}")
    private String jwtSecret;

    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }
        User user = new User();
        user.setEmail(req.getEmail());
        user.setPassword(hashPassword(req.getPassword()));
        User saved = userRepository.save(user);
        return new AuthResponse(generateToken(saved));
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        if (!verifyPassword(req.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        return new AuthResponse(generateToken(user));
    }

    public String generateToken(User user) {
        Base64.Encoder enc = Base64.getUrlEncoder().withoutPadding();
        long now = Instant.now().getEpochSecond();
        String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String payload = "{\"sub\":\"" + escapeJson(user.getEmail()) + "\","
                + "\"iat\":" + now + ","
                + "\"exp\":" + (now + TOKEN_TTL_SECONDS) + "}";
        String unsigned = enc.encodeToString(header.getBytes(StandardCharsets.UTF_8))
                + "." + enc.encodeToString(payload.getBytes(StandardCharsets.UTF_8));
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] sig = mac.doFinal(unsigned.getBytes(StandardCharsets.UTF_8));
            return unsigned + "." + enc.encodeToString(sig);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Token generation failed");
        }
    }

    private String hashPassword(String password) {
        byte[] salt = new byte[SALT_BYTES];
        secureRandom.nextBytes(salt);
        byte[] hash = sha256(salt, password);
        Base64.Encoder enc = Base64.getEncoder();
        return enc.encodeToString(salt) + ":" + enc.encodeToString(hash);
    }

    private boolean verifyPassword(String password, String stored) {
        if (password == null || stored == null) {
            return false;
        }
        String[] parts = stored.split(":", 2);
        if (parts.length != 2) {
            return false;
        }
        try {
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] expected = Base64.getDecoder().decode(parts[1]);
            return MessageDigest.isEqual(expected, sha256(salt, password));
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private byte[] sha256(byte[] salt, String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            return md.digest(password.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    private String escapeJson(String value) {
        return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}