package com.bookstore.api.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "공통 API 응답")
public class ApiResponse<T> {

    @Schema(description = "성공 여부", example = "true")
    @Builder.Default
    private Boolean isSuccess = true;

    @Schema(description = "응답 메시지", example = "정상적으로 처리되었습니다.")
    private String message;

    @Schema(description = "응답 데이터")
    private T payload;

    @Schema(description = "응답 시각", example = "2025-12-14T10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    // 성공 응답 (데이터 포함)
    public static <T> ApiResponse<T> success(T payload) {
        return ApiResponse.<T>builder()
                .isSuccess(true)
                .message("정상적으로 처리되었습니다.")
                .payload(payload)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // 성공 응답 (메시지 + 데이터)
    public static <T> ApiResponse<T> success(String message, T payload) {
        return ApiResponse.<T>builder()
                .isSuccess(true)
                .message(message)
                .payload(payload)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // 성공 응답 (메시지만)
    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .isSuccess(true)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}