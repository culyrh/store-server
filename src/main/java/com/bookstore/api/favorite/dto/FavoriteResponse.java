package com.bookstore.api.favorite.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "찜 응답")
public class FavoriteResponse {

    @Schema(description = "찜 ID", example = "1")
    private Long id;

    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "도서 ID", example = "1")
    private Long bookId;

    @Schema(description = "도서 제목", example = "클린 코드")
    private String bookTitle;

    @Schema(description = "도서 저자", example = "로버트 C. 마틴")
    private String bookAuthor;

    @Schema(description = "도서 가격", example = "33000")
    private BigDecimal bookPrice;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "찜한 일시", example = "2024-01-01 12:00:00")
    private LocalDateTime createdAt;
}