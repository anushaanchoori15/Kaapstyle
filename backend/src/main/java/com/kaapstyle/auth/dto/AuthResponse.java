package com.kaapstyle.auth.dto;

public record AuthResponse(
        String accessToken,
        String tokenType,
        String email,
        Long userId
) {

    public static AuthResponse bearer(String accessToken, String email, Long userId) {
        return new AuthResponse(accessToken, "Bearer", email, userId);
    }
}