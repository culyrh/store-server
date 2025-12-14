package com.bookstore.api.comment.controller;

import com.bookstore.api.comment.entity.Comment;
import com.bookstore.api.comment.service.CommentService;
import com.bookstore.api.common.dto.ApiResponse;
import com.bookstore.api.common.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Comment", description = "댓글 관리 API")
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "댓글 작성", description = "리뷰에 댓글을 작성합니다")
    @PostMapping("/review/{reviewId}")
    public ResponseEntity<ApiResponse<Comment>> createComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "리뷰 ID") @PathVariable Long reviewId,
            @Parameter(description = "댓글 내용") @RequestParam String content) {
        Long userId = Long.parseLong(userDetails.getUsername());
        Comment comment = commentService.createComment(userId, reviewId, content);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("댓글이 작성되었습니다", comment));
    }

    @Operation(summary = "리뷰별 댓글 목록", description = "특정 리뷰의 댓글 목록을 조회합니다")
    @GetMapping("/review/{reviewId}")
    public ResponseEntity<ApiResponse<PageResponse<Comment>>> getCommentsByReview(
            @Parameter(description = "리뷰 ID") @PathVariable Long reviewId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Comment> page = commentService.getCommentsByReview(reviewId, pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(page)));
    }

    @Operation(summary = "댓글 수정", description = "작성한 댓글을 수정합니다")
    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Comment>> updateComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "댓글 ID") @PathVariable Long commentId,
            @Parameter(description = "댓글 내용") @RequestParam String content) {
        Long userId = Long.parseLong(userDetails.getUsername());
        Comment comment = commentService.updateComment(userId, commentId, content);
        return ResponseEntity.ok(ApiResponse.success("댓글이 수정되었습니다", comment));
    }

    @Operation(summary = "댓글 삭제", description = "작성한 댓글을 삭제합니다")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "댓글 ID") @PathVariable Long commentId) {
        Long userId = Long.parseLong(userDetails.getUsername());
        commentService.deleteComment(userId, commentId);
        return ResponseEntity.ok(ApiResponse.<Void>success("댓글이 삭제되었습니다"));
    }
}