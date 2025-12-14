package com.bookstore.api.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "리뷰 수정 요청")
public class UpdateReviewRequest {

    @Min(value = 1, message = "평점은 1점 이상이어야 합니다")
    @Max(value = 5, message = "평점은 5점 이하여야 합니다")
    @Schema(description = "평점 (1-5)", example = "4")
    private Integer rating;

    @Schema(description = "리뷰 내용", example = "수정된 리뷰 내용입니다.")
    private String comment;
}