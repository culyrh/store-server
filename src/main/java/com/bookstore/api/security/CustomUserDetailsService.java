package com.bookstore.api.security;

import com.bookstore.api.common.exception.BusinessException;
import com.bookstore.api.common.exception.ErrorCode;
import com.bookstore.api.user.entity.User;
import com.bookstore.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("사용자 인증 정보 로드: {}", email);

        User user = userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USER_NOT_FOUND,
                        "해당 이메일의 사용자를 찾을 수 없습니다: " + email
                ));

        // 탈퇴한 사용자 체크
        if (user.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "탈퇴한 사용자입니다.");
        }

        // 활성화된 사용자 체크
        if (!user.getIsActive()) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "비활성화된 사용자입니다.");
        }

        return createUserDetails(user);
    }

    /**
     * User 엔티티를 UserDetails로 변환
     */
    private UserDetails createUserDetails(User user) {
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!user.getIsActive())
                .build();
    }
}