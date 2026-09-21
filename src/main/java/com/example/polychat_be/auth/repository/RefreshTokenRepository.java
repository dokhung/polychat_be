package com.example.polychat_be.auth.repository;

import com.example.polychat_be.auth.entity.RefreshToken;
import com.example.polychat_be.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    void deleteAllByUser(User user);
}
