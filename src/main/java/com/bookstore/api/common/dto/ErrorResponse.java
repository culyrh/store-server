package com.bookstore.api.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "에러 응답")
public class ErrorResponse {

    @Schema(description = "에러 발생 시각", example = "2025-12-14T10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    @Schema(description = "요청 경로", example = "/api/books/1")
    private String path;

    @Schema(description = "HTTP 상태 코드", example = "404")
    private Integer status;

    @Schema(description = "에러 코드", example = "BOOK_NOT_FOUND")
    private String code;

    @Schema(description = "에러 메시지", example = "도서를 찾을 수 없습니다.")
    private String message;

    @Schema(description = "상세 정보 (선택)")
    private Map<String, String> details;
}