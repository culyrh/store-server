package com.bookstore.api.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "리뷰 작성 요청")
public class CreateReviewRequest {

    @NotNull(message = "도서 ID는 필수입니다")
    @Schema(description = "도서 ID", example = "1")
    private Long bookId;

    @NotNull(message = "평점은 필수입니다")
    @Min(value = 1, message = "평점은 1점 이상이어야 합니다")
    @Max(value = 5, message = "평점은 5점 이하여야 합니다")
    @Schema(description = "평점 (1-5)", example = "5")
    private Integer rating;

    @NotBlank(message = "리뷰 내용은 필수입니다")
    @Schema(description = "리뷰 내용", example = "정말 좋은 책입니다!")
    private String comment;
}