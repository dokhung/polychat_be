package com.example.polychat_be.auth.controller;

import com.example.polychat_be.auth.dto.request.LoginRequestDTO;
import com.example.polychat_be.auth.dto.request.RefreshTokenRequestDTO;
import com.example.polychat_be.auth.dto.request.SignupRequestDTO;
import com.example.polychat_be.auth.dto.response.TokenResponseDTO;
import com.example.polychat_be.auth.dto.response.UserResponseDTO;
import com.example.polychat_be.auth.dto.response.EmailAvailabilityResponseDTO;
import com.example.polychat_be.auth.entity.User;
import com.example.polychat_be.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService auth;

    @GetMapping("/email-availability")
    public EmailAvailabilityResponseDTO checkEmail(@RequestParam String email) {
        return auth.checkEmail(email);
    }

    @PostMapping("/signup")
    public TokenResponseDTO signup(@Valid @RequestBody SignupRequestDTO request) {
        return auth.signup(request);
    }

    @PostMapping("/login")
    public TokenResponseDTO login(@Valid @RequestBody LoginRequestDTO request) {
        return auth.login(request);
    }

    @PostMapping("/refresh")
    public TokenResponseDTO refresh(@Valid @RequestBody RefreshTokenRequestDTO request) {
        return auth.refresh(request.refreshToken());
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestBody(required = false) RefreshTokenRequestDTO request) {
        if (request != null) auth.logout(request.refreshToken());
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal User user) {
        auth.delete(user);
    }

    @GetMapping("/me")
    public UserResponseDTO me(@AuthenticationPrincipal User user) {
        return UserResponseDTO.from(user);
    }
}
