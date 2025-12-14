package com.bookstore.api.review.controller;

import com.bookstore.api.common.dto.ApiResponse;
import com.bookstore.api.common.dto.PageResponse;
import com.bookstore.api.review.dto.CreateReviewRequest;
import com.bookstore.api.review.dto.ReviewResponse;
import com.bookstore.api.review.dto.UpdateReviewRequest;
import com.bookstore.api.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Review", description = "리뷰 관리 API")
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "리뷰 작성", description = "도서에 대한 리뷰를 작성합니다")
    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateReviewRequest request) {
        Long userId = Long.parseLong(userDetails.getUsername());
        ReviewResponse response = reviewService.createReview(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("리뷰가 작성되었습니다", response));
    }

    @Operation(summary = "도서별 리뷰 목록", description = "특정 도서의 리뷰 목록을 조회합니다")
    @GetMapping("/book/{bookId}")
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> getReviewsByBook(
            @Parameter(description = "도서 ID") @PathVariable Long bookId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ReviewResponse> page = reviewService.getReviewsByBook(bookId, pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(page)));
    }

    @Operation(summary = "내 리뷰 목록", description = "내가 작성한 리뷰 목록을 조회합니다")
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> getMyReviews(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Long userId = Long.parseLong(userDetails.getUsername());
        Page<ReviewResponse> page = reviewService.getMyReviews(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(page)));
    }

    @Operation(summary = "리뷰 수정", description = "작성한 리뷰를 수정합니다")
    @PutMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "리뷰 ID") @PathVariable Long reviewId,
            @Valid @RequestBody UpdateReviewRequest request) {
        Long userId = Long.parseLong(userDetails.getUsername());
        ReviewResponse response = reviewService.updateReview(userId, reviewId, request);
        return ResponseEntity.ok(ApiResponse.success("리뷰가 수정되었습니다", response));
    }

    @Operation(summary = "리뷰 삭제", description = "작성한 리뷰를 삭제합니다")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "리뷰 ID") @PathVariable Long reviewId) {
        Long userId = Long.parseLong(userDetails.getUsername());
        reviewService.deleteReview(userId, reviewId);
        return ResponseEntity.ok(ApiResponse.<Void>success("리뷰가 삭제되었습니다"));
    }
}