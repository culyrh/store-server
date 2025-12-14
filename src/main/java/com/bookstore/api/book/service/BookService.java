package com.bookstore.api.book.service;

import com.bookstore.api.book.dto.BookResponse;
import com.bookstore.api.book.dto.CreateBookRequest;
import com.bookstore.api.book.dto.UpdateBookRequest;
import com.bookstore.api.book.entity.Book;
import com.bookstore.api.book.repository.BookRepository;
import com.bookstore.api.common.exception.BusinessException;
import com.bookstore.api.common.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;
    private final ObjectMapper objectMapper;

    /**
     * 도서 생성
     */
    @Transactional
    public BookResponse createBook(CreateBookRequest request) {
        // ISBN 중복 체크
        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE, "이미 등록된 ISBN입니다");
        }

        Book book = Book.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .publisher(request.getPublisher())
                .summary(request.getSummary())
                .isbn(request.getIsbn())
                .price(request.getPrice())
                .publicationDate(request.getPublicationDate())
                .sellerId(request.getSellerId())
                .categories(convertCategoriesToJson(request.getCategoryIds()))
                .build();

        Book savedBook = bookRepository.save(book);
        log.info("도서 생성 완료: {}", savedBook.getId());

        return convertToResponse(savedBook);
    }

    /**
     * 도서 조회
     */
    public BookResponse getBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "도서를 찾을 수 없습니다"));
        return convertToResponse(book);
    }

    /**
     * 도서 수정
     */
    @Transactional
    public BookResponse updateBook(Long id, UpdateBookRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "도서를 찾을 수 없습니다"));

        book.update(
                request.getTitle() != null ? request.getTitle() : book.getTitle(),
                request.getAuthor() != null ? request.getAuthor() : book.getAuthor(),
                request.getPublisher() != null ? request.getPublisher() : book.getPublisher(),
                request.getSummary() != null ? request.getSummary() : book.getSummary(),
                request.getPrice() != null ? request.getPrice() : book.getPrice(),
                request.getPublicationDate() != null ? request.getPublicationDate() : book.getPublicationDate(),
                request.getSellerId() != null ? request.getSellerId() : book.getSellerId(),
                request.getCategoryIds() != null ? convertCategoriesToJson(request.getCategoryIds()) : book.getCategories()
        );

        log.info("도서 수정 완료: {}", id);
        return convertToResponse(book);
    }

    /**
     * 도서 삭제 (소프트 삭제)
     */
    @Transactional
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "도서를 찾을 수 없습니다"));

        bookRepository.delete(book);
        log.info("도서 삭제 완료: {}", id);
    }

    /**
     * 전체 도서 목록 조회 (페이징)
     */
    public Page<BookResponse> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .map(this::convertToResponse);
    }

    /**
     * 도서 검색 (제목, 저자, 출판사)
     */
    public Page<BookResponse> searchBooks(String keyword, Pageable pageable) {
        return bookRepository.searchBooks(keyword, pageable)
                .map(this::convertToResponse);
    }

    /**
     * 제목으로 검색
     */
    public Page<BookResponse> searchByTitle(String title, Pageable pageable) {
        return bookRepository.findByTitleContainingIgnoreCase(title, pageable)
                .map(this::convertToResponse);
    }

    /**
     * 저자로 검색
     */
    public Page<BookResponse> searchByAuthor(String author, Pageable pageable) {
        return bookRepository.findByAuthorContainingIgnoreCase(author, pageable)
                .map(this::convertToResponse);
    }

    /**
     * 출판사로 검색
     */
    public Page<BookResponse> searchByPublisher(String publisher, Pageable pageable) {
        return bookRepository.findByPublisherContainingIgnoreCase(publisher, pageable)
                .map(this::convertToResponse);
    }

    /**
     * 판매자의 도서 목록 조회
     */
    public Page<BookResponse> getBooksBySeller(Long sellerId, Pageable pageable) {
        return bookRepository.findBySellerId(sellerId, pageable)
                .map(this::convertToResponse);
    }

    /**
     * Book -> BookResponse 변환
     */
    private BookResponse convertToResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .publisher(book.getPublisher())
                .summary(book.getSummary())
                .isbn(book.getIsbn())
                .price(book.getPrice())
                .publicationDate(book.getPublicationDate())
                .sellerId(book.getSellerId())
                .categoryIds(convertJsonToCategories(book.getCategories()))
                .createdAt(book.getCreatedAt())
                .updatedAt(book.getUpdatedAt())
                .build();
    }

    /**
     * 카테고리 ID 리스트 -> JSON 문자열 변환
     */
    private String convertCategoriesToJson(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(categoryIds);
        } catch (JsonProcessingException e) {
            log.error("카테고리 JSON 변환 실패", e);
            return null;
        }
    }

    /**
     * JSON 문자열 -> 카테고리 ID 리스트 변환
     */
    private List<Long> convertJsonToCategories(String categoriesJson) {
        if (categoriesJson == null || categoriesJson.isEmpty()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(categoriesJson,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Long.class));
        } catch (JsonProcessingException e) {
            log.error("카테고리 JSON 파싱 실패", e);
            return List.of();
        }
    }
}