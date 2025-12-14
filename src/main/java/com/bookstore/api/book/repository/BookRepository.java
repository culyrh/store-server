package com.bookstore.api.book.repository;

import com.bookstore.api.book.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

    boolean existsByIsbn(String isbn);

    // 제목으로 검색
    Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    // 저자로 검색
    Page<Book> findByAuthorContainingIgnoreCase(String author, Pageable pageable);

    // 출판사로 검색
    Page<Book> findByPublisherContainingIgnoreCase(String publisher, Pageable pageable);

    // 판매자로 검색
    Page<Book> findBySellerId(Long sellerId, Pageable pageable);

    // 통합 검색 (제목, 저자, 출판사)
    @Query("SELECT b FROM Book b WHERE " +
            "LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.publisher) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Book> searchBooks(@Param("keyword") String keyword, Pageable pageable);
}