package com.bookstore.api.book.controller;

import com.bookstore.api.book.dto.BookResponse;
import com.bookstore.api.book.dto.CreateBookRequest;
import com.bookstore.api.book.dto.UpdateBookRequest;
import com.bookstore.api.book.service.BookService;
import com.bookstore.api.common.dto.ApiResponse;
import com.bookstore.api.common.dto.PageResponse;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Book", description = "도서 관리 API")
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @Operation(summary = "도서 생성", description = "새로운 도서를 등록합니다 (ADMIN 권한 필요)")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BookResponse>> createBook(
            @Valid @RequestBody CreateBookRequest request) {
        BookResponse response = bookService.createBook(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("도서가 생성되었습니다", response));
    }

    @Operation(summary = "도서 조회", description = "도서 ID로 도서를 조회합니다")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponse>> getBook(
            @Parameter(description = "도서 ID") @PathVariable Long id) {
        BookResponse response = bookService.getBook(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "도서 수정", description = "도서 정보를 수정합니다 (ADMIN 권한 필요)")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BookResponse>> updateBook(
            @Parameter(description = "도서 ID") @PathVariable Long id,
            @Valid @RequestBody UpdateBookRequest request) {
        BookResponse response = bookService.updateBook(id, request);
        return ResponseEntity.ok(ApiResponse.success("도서가 수정되었습니다", response));
    }

    @Operation(summary = "도서 삭제", description = "도서를 삭제합니다 (ADMIN 권한 필요)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteBook(
            @Parameter(description = "도서 ID") @PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok(ApiResponse.success("도서가 삭제되었습니다", null));
    }

    @Operation(summary = "전체 도서 목록 조회", description = "전체 도서 목록을 페이징하여 조회합니다")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<BookResponse>>> getAllBooks(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<BookResponse> page = bookService.getAllBooks(pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(page)));
    }

    @Operation(summary = "도서 통합 검색", description = "제목, 저자, 출판사로 도서를 검색합니다")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<BookResponse>>> searchBooks(
            @Parameter(description = "검색 키워드") @RequestParam String keyword,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<BookResponse> page = bookService.searchBooks(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(page)));
    }

    @Operation(summary = "제목으로 도서 검색", description = "제목으로 도서를 검색합니다")
    @GetMapping("/search/title")
    public ResponseEntity<ApiResponse<PageResponse<BookResponse>>> searchByTitle(
            @Parameter(description = "제목") @RequestParam String title,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<BookResponse> page = bookService.searchByTitle(title, pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(page)));
    }

    @Operation(summary = "저자로 도서 검색", description = "저자로 도서를 검색합니다")
    @GetMapping("/search/author")
    public ResponseEntity<ApiResponse<PageResponse<BookResponse>>> searchByAuthor(
            @Parameter(description = "저자") @RequestParam String author,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<BookResponse> page = bookService.searchByAuthor(author, pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(page)));
    }

    @Operation(summary = "출판사로 도서 검색", description = "출판사로 도서를 검색합니다")
    @GetMapping("/search/publisher")
    public ResponseEntity<ApiResponse<PageResponse<BookResponse>>> searchByPublisher(
            @Parameter(description = "출판사") @RequestParam String publisher,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<BookResponse> page = bookService.searchByPublisher(publisher, pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(page)));
    }

    @Operation(summary = "판매자의 도서 목록 조회", description = "특정 판매자의 도서 목록을 조회합니다")
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<ApiResponse<PageResponse<BookResponse>>> getBooksBySeller(
            @Parameter(description = "판매자 ID") @PathVariable Long sellerId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<BookResponse> page = bookService.getBooksBySeller(sellerId, pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(page)));
    }
}