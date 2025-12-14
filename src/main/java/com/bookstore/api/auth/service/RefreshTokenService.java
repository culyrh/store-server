package com.bookstore.api.auth.service;

import com.bookstore.api.common.exception.BusinessException;
import com.bookstore.api.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenValidityInSeconds;

    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";

    /**
     * Refresh Token 저장
     */
    public void saveRefreshToken(String email, String token) {
        String key = REFRESH_TOKEN_PREFIX + email;
        redisTemplate.opsForValue().set(
                key,
                token,
                refreshTokenValidityInSeconds,
                TimeUnit.SECONDS
        );
        log.debug("Refresh Token 저장: email={}", email);
    }

    /**
     * Refresh Token 조회
     */
    public String getRefreshToken(String email) {
        String key = REFRESH_TOKEN_PREFIX + email;
        String token = redisTemplate.opsForValue().get(key);

        if (token == null) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN, "유효하지 않은 Refresh Token입니다.");
        }

        return token;
    }

    /**
     * Refresh Token 검증
     */
    public boolean validateRefreshToken(String email, String token) {
        try {
            String storedToken = getRefreshToken(email);
            return storedToken.equals(token);
        } catch (BusinessException e) {
            return false;
        }
    }

    /**
     * Refresh Token 삭제
     */
    public void deleteRefreshToken(String email) {
        String key = REFRESH_TOKEN_PREFIX + email;
        redisTemplate.delete(key);
        log.debug("Refresh Token 삭제: email={}", email);
    }

    /**
     * 특정 사용자의 모든 Refresh Token 삭제
     */
    public void deleteAllRefreshTokens(String email) {
        deleteRefreshToken(email);
    }
}