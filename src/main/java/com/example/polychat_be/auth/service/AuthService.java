package com.example.polychat_be.auth.service;

import com.example.polychat_be.auth.dto.request.LoginRequestDTO;
import com.example.polychat_be.auth.dto.request.SignupRequestDTO;
import com.example.polychat_be.auth.dto.response.TokenResponseDTO;
import com.example.polychat_be.auth.dto.response.EmailAvailabilityResponseDTO;
import com.example.polychat_be.auth.entity.RefreshToken;
import com.example.polychat_be.auth.entity.User;
import com.example.polychat_be.auth.repository.RefreshTokenRepository;
import com.example.polychat_be.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository users;
    private final RefreshTokenRepository refreshTokens;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    @Transactional
    public TokenResponseDTO signup(SignupRequestDTO request) {
        String email = request.email().trim().toLowerCase();
        if (users.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 가입된 이메일입니다.");
        }

        String secondaryPassword = blankToNull(request.secondaryPassword());
        User user = User.builder()
                .email(email)
                .passwordHash(encoder.encode(request.password()))
                .secondaryPasswordHash(secondaryPassword == null ? null : encoder.encode(secondaryPassword))
                .build();
        users.save(user);
        return issue(user);
    }

    @Transactional(readOnly = true)
    public EmailAvailabilityResponseDTO checkEmail(String email) {
        String normalizedEmail = email.trim().toLowerCase();
        boolean available = !users.existsByEmailIgnoreCase(normalizedEmail);
        String message = available ? "사용 가능한 이메일입니다." : "이미 가입된 이메일입니다.";
        return new EmailAvailabilityResponseDTO(available, message);
    }

    @Transactional
    public TokenResponseDTO login(LoginRequestDTO request) {
        User user = users.findByEmailIgnoreCase(request.email().trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."));
        if (!encoder.matches(request.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다.");
        }
        return issue(user);
    }

    @Transactional
    public TokenResponseDTO refresh(String token) {
        if (!jwt.isValid(token) || !jwt.isRefresh(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다.");
        }

        RefreshToken saved = refreshTokens.findByTokenHash(hash(token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "세션이 만료되었습니다."));
        if (saved.isRevoked() || saved.getExpiresAt().isBefore(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "세션이 만료되었습니다.");
        }
        saved.setRevoked(true);
        return issue(saved.getUser());
    }

    @Transactional
    public void logout(String token) {
        if (token != null) {
            refreshTokens.findByTokenHash(hash(token)).ifPresent(refreshToken -> refreshToken.setRevoked(true));
        }
    }

    @Transactional
    public void delete(User user) {
        refreshTokens.deleteAllByUser(user);
        users.delete(user);
    }

    public User requireUser(String email) {
        return users.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }

    private TokenResponseDTO issue(User user) {
        String refresh = jwt.createRefreshToken(user);
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .tokenHash(hash(refresh))
                .expiresAt(Instant.now().plusMillis(604800000))
                .revoked(false)
                .build();
        refreshTokens.save(refreshToken);
        return new TokenResponseDTO(jwt.createAccessToken(user), refresh, jwt.getAccessExpiration() / 1000);
    }

    private String hash(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (Exception exception) {
            throw new IllegalStateException("토큰 해시에 실패했습니다.", exception);
        }
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
