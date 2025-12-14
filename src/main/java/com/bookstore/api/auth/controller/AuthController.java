package com.bookstore.api.auth.controller;

import com.bookstore.api.auth.dto.*;
import com.bookstore.api.auth.service.AuthService;
import com.bookstore.api.common.dto.ApiResponse;
import com.bookstore.api.user.dto.UserProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "인증", description = "인증 관련 API")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "회원가입 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "입력값 검증 실패",
                    content = @Content(schema = @Schema(implementation = com.bookstore.api.common.dto.ErrorResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "이메일 중복",
                    content = @Content(schema = @Schema(implementation = com.bookstore.api.common.dto.ErrorResponse.class))
            )
    })
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<UserProfileResponse> signup(@Valid @RequestBody SignupRequest request) {
        log.info("회원가입 요청: {}", request.getEmail());
        UserProfileResponse response = authService.signup(request);
        return ApiResponse.success("회원가입이 완료되었습니다.", response);
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = com.bookstore.api.common.dto.ErrorResponse.class))
            )
    })
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("로그인 요청: {}", request.getEmail());
        LoginResponse response = authService.login(request);
        return ApiResponse.success("로그인에 성공했습니다.", response);
    }

    @Operation(summary = "토큰 갱신", description = "Refresh Token으로 새로운 Access Token을 발급받습니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "토큰 갱신 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "유효하지 않거나 만료된 토큰",
                    content = @Content(schema = @Schema(implementation = com.bookstore.api.common.dto.ErrorResponse.class))
            )
    })
    @PostMapping("/refresh")
    public ApiResponse<TokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("토큰 갱신 요청");
        TokenResponse response = authService.refresh(request);
        return ApiResponse.success("토큰이 갱신되었습니다.", response);
    }

    @Operation(summary = "로그아웃", description = "현재 사용자를 로그아웃합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "로그아웃 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = com.bookstore.api.common.dto.ErrorResponse.class))
            )
    })
    @PostMapping("/logout")
    public ApiResponse<Void> logout(Authentication authentication) {
        String email = authentication.getName();
        log.info("로그아웃 요청: {}", email);
        authService.logout(email);
        return ApiResponse.success("로그아웃되었습니다.");
    }
}