package com.bookstore.api.auth.service;

import com.bookstore.api.auth.dto.*;
import com.bookstore.api.auth.entity.RefreshToken;
import com.bookstore.api.auth.repository.RefreshTokenRepository;
import com.bookstore.api.common.exception.BusinessException;
import com.bookstore.api.common.exception.ErrorCode;
import com.bookstore.api.security.jwt.JwtTokenProvider;
import com.bookstore.api.user.dto.UserProfileResponse;
import com.bookstore.api.user.entity.User;
import com.bookstore.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    /**
     * 회원가입
     */
    @Transactional
    public UserProfileResponse signup(SignupRequest request) {
        log.info("회원가입 시도: {}", request.getEmail());

        // 1. 비밀번호 확인 일치 검증
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new BusinessException(
                    ErrorCode.INVALID_INPUT_VALUE,
                    "비밀번호와 비밀번호 확인이 일치하지 않습니다.",
                    Map.of("passwordConfirm", "비밀번호가 일치하지 않습니다.")
            );
        }

        // 2. 이메일 중복 검사
        if (userRepository.existsByEmailAndDeletedAtIsNull(request.getEmail())) {
            throw new BusinessException(
                    ErrorCode.DUPLICATE_EMAIL,
                    "이미 사용 중인 이메일입니다.",
                    Map.of("email", request.getEmail())
            );
        }

        // 3. 사용자 생성
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .birthDate(request.getBirthDate())
                .gender(request.getGender())
                .address(request.getAddress())
                .phoneNumber(request.getPhoneNumber())
                .build();

        User savedUser = userRepository.save(user);
        log.info("회원가입 완료: userId={}", savedUser.getId());

        return UserProfileResponse.from(savedUser);
    }

    /**
     * 로그인
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        log.info("로그인 시도: {}", request.getEmail());

        // 1. 인증
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // 2. 사용자 조회
        User user = userRepository.findByEmailAndDeletedAtIsNull(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 3. 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(
                user.getEmail(),
                user.getRole().name()
        );
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

        // 4. Refresh Token 저장
        saveRefreshToken(user.getId(), refreshToken);

        log.info("로그인 성공: userId={}", user.getId());

        return LoginResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtTokenProvider.getExpirationTime(accessToken))
                .loginAt(LocalDateTime.now())
                .build();
    }

    /**
     * 토큰 갱신
     */
    @Transactional
    public TokenResponse refresh(RefreshTokenRequest request) {
        log.info("토큰 갱신 시도");

        // 1. Refresh Token 검증
        String refreshToken = request.getRefreshToken();
        jwtTokenProvider.validateToken(refreshToken);

        if (!jwtTokenProvider.isRefreshToken(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN, "Refresh Token이 아닙니다.");
        }

        // 2. Refresh Token 존재 여부 확인
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN, "유효하지 않은 Refresh Token입니다."));

        // 3. 만료 확인
        if (storedToken.isExpired()) {
            refreshTokenRepository.delete(storedToken);
            throw new BusinessException(ErrorCode.TOKEN_EXPIRED, "Refresh Token이 만료되었습니다.");
        }

        // 4. 사용자 조회
        String email = jwtTokenProvider.getEmailFromToken(refreshToken);
        User user = userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 5. 새 토큰 생성
        String newAccessToken = jwtTokenProvider.createAccessToken(
                user.getEmail(),
                user.getRole().name()
        );
        String newRefreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

        // 6. 새 Refresh Token 저장
        refreshTokenRepository.delete(storedToken);
        saveRefreshToken(user.getId(), newRefreshToken);

        log.info("토큰 갱신 성공: userId={}", user.getId());

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .expiresIn(jwtTokenProvider.getExpirationTime(newAccessToken))
                .build();
    }

    /**
     * 로그아웃
     */
    @Transactional
    public void logout(String email) {
        log.info("로그아웃 시도: {}", email);

        User user = userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // Refresh Token 삭제
        refreshTokenRepository.deleteByUserId(user.getId());

        log.info("로그아웃 성공: userId={}", user.getId());
    }

    /**
     * Refresh Token 저장
     */
    private void saveRefreshToken(Long userId, String token) {
        // 기존 토큰 삭제
        refreshTokenRepository.deleteByUserId(userId);

        // 새 토큰 저장
        Long expirationSeconds = jwtTokenProvider.getExpirationTime(token);
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(expirationSeconds);

        RefreshToken refreshToken = RefreshToken.builder()
                .userId(userId)
                .token(token)
                .expiresAt(expiresAt)
                .build();

        refreshTokenRepository.save(refreshToken);
    }
}