package com.bookstore.api.security.jwt;

import com.bookstore.api.common.exception.BusinessException;
import com.bookstore.api.common.exception.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            // 1. Request Header에서 JWT 토큰 추출
            String jwt = resolveToken(request);

            // 2. 토큰이 존재하고 유효한 경우
            if (StringUtils.hasText(jwt)) {
                // 2-1. 토큰 검증
                jwtTokenProvider.validateToken(jwt);

                // 2-2. Access Token인지 확인
                if (!jwtTokenProvider.isAccessToken(jwt)) {
                    throw new BusinessException(
                            ErrorCode.INVALID_TOKEN,
                            "Access Token이 아닙니다."
                    );
                }

                // 2-3. 인증 정보를 SecurityContext에 저장
                Authentication authentication = jwtTokenProvider.getAuthentication(jwt);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("Security Context에 '{}' 인증 정보를 저장했습니다.",
                        authentication.getName());
            }
        } catch (BusinessException e) {
            log.error("JWT 인증 실패: {}", e.getMessage());
            request.setAttribute("exception", e.getErrorCode());
        } catch (Exception e) {
            log.error("JWT 인증 중 예외 발생: {}", e.getMessage(), e);
            request.setAttribute("exception", ErrorCode.INTERNAL_SERVER_ERROR);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Request Header에서 토큰 정보 추출
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }

        return null;
    }
}