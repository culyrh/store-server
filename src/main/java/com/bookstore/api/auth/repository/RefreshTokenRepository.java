package com.bookstore.api.auth.repository;

import com.bookstore.api.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * 토큰으로 조회
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * 사용자 ID로 조회
     */
    Optional<RefreshToken> findByUserId(Long userId);

    /**
     * 사용자 ID로 토큰 삭제
     */
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    /**
     * 만료된 토큰 삭제
     */
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiresAt < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);

    /**
     * 토큰 존재 여부 확인
     */
    boolean existsByToken(String token);
}