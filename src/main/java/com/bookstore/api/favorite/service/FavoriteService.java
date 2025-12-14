package com.bookstore.api.favorite.service;

import com.bookstore.api.book.entity.Book;
import com.bookstore.api.book.repository.BookRepository;
import com.bookstore.api.common.exception.BusinessException;
import com.bookstore.api.common.exception.ErrorCode;
import com.bookstore.api.favorite.dto.FavoriteResponse;
import com.bookstore.api.favorite.entity.Favorite;
import com.bookstore.api.favorite.repository.FavoriteRepository;
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
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final BookRepository bookRepository;

    /**
     * 찜 추가
     */
    @Transactional
    public void addFavorite(Long userId, Long bookId) {
        // 중복 확인
        if (favoriteRepository.existsByUserIdAndBookId(userId, bookId)) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE, "이미 찜한 도서입니다");
        }

        // 도서 존재 확인
        bookRepository.findById(bookId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "도서를 찾을 수 없습니다"));

        Favorite favorite = Favorite.builder()
                .userId(userId)
                .bookId(bookId)
                .build();

        favoriteRepository.save(favorite);
        log.info("찜 추가: userId={}, bookId={}", userId, bookId);
    }

    /**
     * 찜 삭제
     */
    @Transactional
    public void removeFavorite(Long userId, Long bookId) {
        Favorite favorite = favoriteRepository.findByUserIdAndBookId(userId, bookId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "찜 목록에 없는 도서입니다"));

        favoriteRepository.delete(favorite);
        log.info("찜 삭제: userId={}, bookId={}", userId, bookId);
    }

    /**
     * 내 찜 목록 조회
     */
    public Page<FavoriteResponse> getMyFavorites(Long userId, Pageable pageable) {
        return favoriteRepository.findByUserId(userId, pageable)
                .map(favorite -> {
                    Book book = bookRepository.findById(favorite.getBookId())
                            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "도서를 찾을 수 없습니다"));
                    return convertToResponse(favorite, book);
                });
    }

    /**
     * 찜 여부 확인
     */
    public boolean isFavorite(Long userId, Long bookId) {
        return favoriteRepository.existsByUserIdAndBookId(userId, bookId);
    }

    /**
     * Favorite -> FavoriteResponse 변환
     */
    private FavoriteResponse convertToResponse(Favorite favorite, Book book) {
        return FavoriteResponse.builder()
                .id(favorite.getId())
                .userId(favorite.getUserId())
                .bookId(favorite.getBookId())
                .bookTitle(book.getTitle())
                .bookAuthor(book.getAuthor())
                .bookPrice(book.getPrice())
                .createdAt(favorite.getCreatedAt())
                .build();
    }
}