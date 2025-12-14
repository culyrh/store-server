package com.bookstore.api.user.controller;

import com.bookstore.api.common.dto.ApiResponse;
import com.bookstore.api.common.dto.PageResponse;
import com.bookstore.api.user.dto.UpdatePasswordRequest;
import com.bookstore.api.user.dto.UpdateProfileRequest;
import com.bookstore.api.user.dto.UserProfileResponse;
import com.bookstore.api.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
@Tag(name = "사용자", description = "사용자 관련 API")
public class UserController {

    private final UserService userService;

    @Operation(summary = "내 프로필 조회", description = "현재 로그인한 사용자의 프로필을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = com.bookstore.api.common.dto.ErrorResponse.class))
            )
    })
    @GetMapping("/me")
    public ApiResponse<UserProfileResponse> getMyProfile(Authentication authentication) {
        String email = authentication.getName();
        log.info("내 프로필 조회: {}", email);
        UserProfileResponse response = userService.getProfile(email);
        return ApiResponse.success(response);
    }

    @Operation(summary = "사용자 프로필 조회", description = "특정 사용자의 프로필을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = com.bookstore.api.common.dto.ErrorResponse.class))
            )
    })
    @GetMapping("/{userId}")
    public ApiResponse<UserProfileResponse> getUserProfile(
            @Parameter(description = "사용자 ID", example = "1")
            @PathVariable Long userId
    ) {
        log.info("사용자 프로필 조회: userId={}", userId);
        UserProfileResponse response = userService.getProfileById(userId);
        return ApiResponse.success(response);
    }

    @Operation(summary = "프로필 수정", description = "현재 로그인한 사용자의 프로필을 수정합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "수정 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "입력값 검증 실패",
                    content = @Content(schema = @Schema(implementation = com.bookstore.api.common.dto.ErrorResponse.class))
            )
    })
    @PatchMapping("/me")
    public ApiResponse<UserProfileResponse> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        String email = authentication.getName();
        log.info("프로필 수정: {}", email);
        UserProfileResponse response = userService.updateProfile(email, request);
        return ApiResponse.success("프로필이 수정되었습니다.", response);
    }

    @Operation(summary = "비밀번호 변경", description = "현재 로그인한 사용자의 비밀번호를 변경합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "변경 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "입력값 검증 실패 또는 비밀번호 불일치",
                    content = @Content(schema = @Schema(implementation = com.bookstore.api.common.dto.ErrorResponse.class))
            )
    })
    @PatchMapping("/me/password")
    public ApiResponse<Void> updatePassword(
            Authentication authentication,
            @Valid @RequestBody UpdatePasswordRequest request
    ) {
        String email = authentication.getName();
        log.info("비밀번호 변경: {}", email);
        userService.updatePassword(email, request);
        return ApiResponse.success("비밀번호가 변경되었습니다.");
    }

    @Operation(summary = "회원 탈퇴", description = "현재 로그인한 사용자의 계정을 탈퇴합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "탈퇴 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "비밀번호 불일치",
                    content = @Content(schema = @Schema(implementation = com.bookstore.api.common.dto.ErrorResponse.class))
            )
    })
    @DeleteMapping("/me")
    public ApiResponse<Void> deleteAccount(
            Authentication authentication,
            @Parameter(description = "비밀번호", required = true)
            @RequestParam String password
    ) {
        String email = authentication.getName();
        log.info("회원 탈퇴: {}", email);
        userService.deleteAccount(email, password);
        return ApiResponse.success("회원 탈퇴가 완료되었습니다.");
    }

    @Operation(summary = "전체 사용자 목록 조회", description = "전체 사용자 목록을 페이징하여 조회합니다.")
    @GetMapping
    public ApiResponse<PageResponse<UserProfileResponse>> getAllUsers(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        log.info("전체 사용자 목록 조회");
        PageResponse<UserProfileResponse> response = userService.getAllUsers(pageable);
        return ApiResponse.success(response);
    }

    @Operation(summary = "사용자 검색 (이름)", description = "이름으로 사용자를 검색합니다.")
    @GetMapping("/search/name")
    public ApiResponse<PageResponse<UserProfileResponse>> searchUsersByName(
            @Parameter(description = "검색 키워드", example = "홍길동")
            @RequestParam String keyword,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        log.info("사용자 검색 (이름): {}", keyword);
        PageResponse<UserProfileResponse> response = userService.searchUsersByName(keyword, pageable);
        return ApiResponse.success(response);
    }

    @Operation(summary = "사용자 검색 (이메일)", description = "이메일로 사용자를 검색합니다.")
    @GetMapping("/search/email")
    public ApiResponse<PageResponse<UserProfileResponse>> searchUsersByEmail(
            @Parameter(description = "검색 키워드", example = "user@example.com")
            @RequestParam String keyword,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        log.info("사용자 검색 (이메일): {}", keyword);
        PageResponse<UserProfileResponse> response = userService.searchUsersByEmail(keyword, pageable);
        return ApiResponse.success(response);
    }
}