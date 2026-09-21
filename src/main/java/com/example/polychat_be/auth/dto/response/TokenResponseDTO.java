package com.example.polychat_be.auth.dto.response;

public record TokenResponseDTO(
        String accessToken,
        String refreshToken,
        long expiresIn
) {
}
