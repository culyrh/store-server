package com.bookstore.api.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "토큰 갱신 요청")
public class RefreshTokenRequest {

    @Schema(description = "Refresh Token", example = "eyJhbGciOiJIUzUxMiJ9...", required = true)
    @NotBlank(message = "Refresh Token을 입력해주세요.")
    private String refreshToken;
}