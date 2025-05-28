package com.datingapp.datingapp.repository;

import com.datingapp.datingapp.entity.RefreshToken;
import com.datingapp.datingapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    int deleteByUser(User user);


    boolean existsByUser_PkUser(Integer userId);

    Optional<RefreshToken> findRefreshTokensByUser_PkUser(Integer userId);
}
