package com.bookstore.api.review.service;

import com.bookstore.api.book.entity.Book;
import com.bookstore.api.book.repository.BookRepository;
import com.bookstore.api.common.exception.BusinessException;
import com.bookstore.api.common.exception.ErrorCode;
import com.bookstore.api.review.dto.CreateReviewRequest;
import com.bookstore.api.review.dto.ReviewResponse;
import com.bookstore.api.review.dto.UpdateReviewRequest;
import com.bookstore.api.review.entity.Review;
import com.bookstore.api.review.repository.ReviewRepository;
import com.bookstore.api.user.entity.User;
import com.bookstore.api.user.repository.UserRepository;
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
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    /**
     * 리뷰 작성
     */
    @Transactional
    public ReviewResponse createReview(Long userId, CreateReviewRequest request) {
        // 중복 리뷰 체크 (1인 1리뷰)
        if (reviewRepository.existsByUserIdAndBookId(userId, request.getBookId())) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE, "이미 리뷰를 작성하셨습니다");
        }

        // 도서 존재 확인
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "도서를 찾을 수 없습니다"));

        // 사용자 존재 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "사용자를 찾을 수 없습니다"));

        Review review = Review.builder()
                .userId(userId)
                .bookId(request.getBookId())
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        Review savedReview = reviewRepository.save(review);
        log.info("리뷰 생성: reviewId={}, userId={}, bookId={}", savedReview.getId(), userId, request.getBookId());

        return convertToResponse(savedReview, user, book);
    }

    /**
     * 도서별 리뷰 목록 조회
     */
    public Page<ReviewResponse> getReviewsByBook(Long bookId, Pageable pageable) {
        return reviewRepository.findByBookId(bookId, pageable)
                .map(review -> {
                    User user = userRepository.findById(review.getUserId()).orElse(null);
                    Book book = bookRepository.findById(review.getBookId()).orElse(null);
                    return convertToResponse(review, user, book);
                });
    }

    /**
     * 내 리뷰 목록 조회
     */
    public Page<ReviewResponse> getMyReviews(Long userId, Pageable pageable) {
        return reviewRepository.findByUserId(userId, pageable)
                .map(review -> {
                    User user = userRepository.findById(userId).orElse(null);
                    Book book = bookRepository.findById(review.getBookId()).orElse(null);
                    return convertToResponse(review, user, book);
                });
    }

    /**
     * 리뷰 수정
     */
    @Transactional
    public ReviewResponse updateReview(Long userId, Long reviewId, UpdateReviewRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "리뷰를 찾을 수 없습니다"));

        // 본인 리뷰 확인
        if (!review.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "본인의 리뷰만 수정할 수 있습니다");
        }

        review.update(
                request.getRating() != null ? request.getRating() : review.getRating(),
                request.getComment() != null ? request.getComment() : review.getComment()
        );

        User user = userRepository.findById(userId).orElse(null);
        Book book = bookRepository.findById(review.getBookId()).orElse(null);

        log.info("리뷰 수정: reviewId={}", reviewId);

        return convertToResponse(review, user, book);
    }

    /**
     * 리뷰 삭제
     */
    @Transactional
    public void deleteReview(Long userId, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "리뷰를 찾을 수 없습니다"));

        // 본인 리뷰 확인
        if (!review.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "본인의 리뷰만 삭제할 수 있습니다");
        }

        reviewRepository.delete(review);
        log.info("리뷰 삭제: reviewId={}", reviewId);
    }

    /**
     * Review -> ReviewResponse 변환
     */
    private ReviewResponse convertToResponse(Review review, User user, Book book) {
        return ReviewResponse.builder()
                .id(review.getId())
                .userId(review.getUserId())
                .userName(user != null ? user.getName() : "알 수 없음")
                .bookId(review.getBookId())
                .bookTitle(book != null ? book.getTitle() : "알 수 없음")
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}