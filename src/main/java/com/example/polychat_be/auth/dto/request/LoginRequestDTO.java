package com.example.polychat_be.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LoginRequestDTO(
        @NotBlank @Email @Pattern(regexp = "[A-Za-z.]+@[A-Za-z.]+") String email,
        @NotBlank String password
) {
}
