package com.example.polychat_be.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SignupRequestDTO(
        @NotBlank @Email @Pattern(regexp = "[A-Za-z.]+@[A-Za-z.]+") String email,
        @NotBlank @Pattern(regexp = "(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,64}") String password,
        @Pattern(regexp = "|(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{4,64}") String secondaryPassword
) {
}
