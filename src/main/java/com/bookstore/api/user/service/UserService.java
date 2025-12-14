package com.bookstore.api.user.service;

import com.bookstore.api.common.dto.PageResponse;
import com.bookstore.api.common.exception.BusinessException;
import com.bookstore.api.common.exception.ErrorCode;
import com.bookstore.api.user.dto.UpdatePasswordRequest;
import com.bookstore.api.user.dto.UpdateProfileRequest;
import com.bookstore.api.user.dto.UserProfileResponse;
import com.bookstore.api.user.entity.User;
import com.bookstore.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 사용자 프로필 조회
     */
    public UserProfileResponse getProfile(String email) {
        log.debug("사용자 프로필 조회: {}", email);

        User user = userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return UserProfileResponse.from(user);
    }

    /**
     * 사용자 ID로 프로필 조회
     */
    public UserProfileResponse getProfileById(Long userId) {
        log.debug("사용자 프로필 조회: userId={}", userId);

        User user = userRepository.findByIdAndNotDeleted(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return UserProfileResponse.from(user);
    }

    /**
     * 프로필 수정
     */
    @Transactional
    public UserProfileResponse updateProfile(String email, UpdateProfileRequest request) {
        log.info("프로필 수정 시도: {}", email);

        User user = userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.updateProfile(
                request.getName(),
                request.getAddress(),
                request.getPhoneNumber(),
                request.getProfileImage()
        );

        log.info("프로필 수정 완료: userId={}", user.getId());
        return UserProfileResponse.from(user);
    }

    /**
     * 비밀번호 변경
     */
    @Transactional
    public void updatePassword(String email, UpdatePasswordRequest request) {
        log.info("비밀번호 변경 시도: {}", email);

        // 1. 새 비밀번호 확인 일치 검증
        if (!request.getNewPassword().equals(request.getNewPasswordConfirm())) {
            throw new BusinessException(
                    ErrorCode.INVALID_INPUT_VALUE,
                    "새 비밀번호와 비밀번호 확인이 일치하지 않습니다."
            );
        }

        // 2. 사용자 조회
        User user = userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 3. 현재 비밀번호 확인
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD, "현재 비밀번호가 일치하지 않습니다.");
        }

        // 4. 비밀번호 변경
        user.updatePassword(passwordEncoder.encode(request.getNewPassword()));

        log.info("비밀번호 변경 완료: userId={}", user.getId());
    }

    /**
     * 회원 탈퇴
     */
    @Transactional
    public void deleteAccount(String email, String password) {
        log.info("회원 탈퇴 시도: {}", email);

        User user = userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 비밀번호 확인
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD, "비밀번호가 일치하지 않습니다.");
        }

        // 소프트 삭제
        user.delete();

        log.info("회원 탈퇴 완료: userId={}", user.getId());
    }

    /**
     * 전체 사용자 조회 (페이징)
     */
    public PageResponse<UserProfileResponse> getAllUsers(Pageable pageable) {
        log.debug("전체 사용자 조회");

        Page<User> users = userRepository.findAllActiveUsers(pageable);
        Page<UserProfileResponse> responsePage = users.map(UserProfileResponse::from);

        return PageResponse.of(responsePage);
    }

    /**
     * 이름으로 사용자 검색
     */
    public PageResponse<UserProfileResponse> searchUsersByName(String keyword, Pageable pageable) {
        log.debug("사용자 검색 (이름): {}", keyword);

        Page<User> users = userRepository.searchByName(keyword, pageable);
        Page<UserProfileResponse> responsePage = users.map(UserProfileResponse::from);

        return PageResponse.of(responsePage);
    }

    /**
     * 이메일로 사용자 검색
     */
    public PageResponse<UserProfileResponse> searchUsersByEmail(String keyword, Pageable pageable) {
        log.debug("사용자 검색 (이메일): {}", keyword);

        Page<User> users = userRepository.searchByEmail(keyword, pageable);
        Page<UserProfileResponse> responsePage = users.map(UserProfileResponse::from);

        return PageResponse.of(responsePage);
    }
}