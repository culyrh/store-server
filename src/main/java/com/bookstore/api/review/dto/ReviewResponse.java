package com.bookstore.api.review.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "리뷰 응답")
public class ReviewResponse {

    @Schema(description = "리뷰 ID", example = "1")
    private Long id;

    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "사용자 이름", example = "홍길동")
    private String userName;

    @Schema(description = "도서 ID", example = "1")
    private Long bookId;

    @Schema(description = "도서 제목", example = "클린 코드")
    private String bookTitle;

    @Schema(description = "평점", example = "5")
    private Integer rating;

    @Schema(description = "리뷰 내용", example = "정말 좋은 책입니다!")
    private String comment;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "작성일시", example = "2024-01-01 12:00:00")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "수정일시", example = "2024-01-01 12:00:00")
    private LocalDateTime updatedAt;
}