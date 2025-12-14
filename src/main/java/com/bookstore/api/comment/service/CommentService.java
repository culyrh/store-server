package com.bookstore.api.comment.service;

import com.bookstore.api.comment.entity.Comment;
import com.bookstore.api.comment.repository.CommentRepository;
import com.bookstore.api.common.exception.BusinessException;
import com.bookstore.api.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;

    /**
     * 댓글 작성
     */
    @Transactional
    public Comment createComment(Long userId, Long reviewId, String content) {
        Comment comment = Comment.builder()
                .userId(userId)
                .reviewId(reviewId)
                .content(content)
                .build();

        Comment savedComment = commentRepository.save(comment);
        log.info("댓글 생성: commentId={}, userId={}, reviewId={}", savedComment.getId(), userId, reviewId);

        return savedComment;
    }

    /**
     * 리뷰별 댓글 목록 조회
     */
    public Page<Comment> getCommentsByReview(Long reviewId, Pageable pageable) {
        return commentRepository.findByReviewId(reviewId, pageable);
    }

    /**
     * 댓글 수정
     */
    @Transactional
    public Comment updateComment(Long userId, Long commentId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "댓글을 찾을 수 없습니다"));

        // 본인 댓글 확인
        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "본인의 댓글만 수정할 수 있습니다");
        }

        comment.update(content);
        log.info("댓글 수정: commentId={}", commentId);

        return comment;
    }

    /**
     * 댓글 삭제
     */
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "댓글을 찾을 수 없습니다"));

        // 본인 댓글 확인
        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "본인의 댓글만 삭제할 수 있습니다");
        }

        commentRepository.delete(comment);
        log.info("댓글 삭제: commentId={}", commentId);
    }
}