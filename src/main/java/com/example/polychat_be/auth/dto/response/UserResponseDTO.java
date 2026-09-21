package com.example.polychat_be.auth.dto.response;

import com.example.polychat_be.auth.entity.User;

import java.time.Instant;

public record UserResponseDTO(Long id, String email, Instant createdAt) {
    public static UserResponseDTO from(User user) {
        return new UserResponseDTO(user.getId(), user.getEmail(), user.getCreatedAt());
    }
}
