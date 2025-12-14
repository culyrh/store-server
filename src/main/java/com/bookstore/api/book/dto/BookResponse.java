package com.bookstore.api.book.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "도서 응답")
public class BookResponse {

    @Schema(description = "도서 ID", example = "1")
    private Long id;

    @Schema(description = "제목", example = "클린 코드")
    private String title;

    @Schema(description = "저자", example = "로버트 C. 마틴")
    private String author;

    @Schema(description = "출판사", example = "인사이트")
    private String publisher;

    @Schema(description = "요약", example = "애자일 소프트웨어 장인 정신")
    private String summary;

    @Schema(description = "ISBN", example = "9788966260959")
    private String isbn;

    @Schema(description = "가격", example = "33000")
    private BigDecimal price;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "출판일", example = "2013-12-24")
    private LocalDate publicationDate;

    @Schema(description = "판매자 ID", example = "1")
    private Long sellerId;

    @Schema(description = "카테고리 ID 목록", example = "[1, 2, 3]")
    private List<Long> categoryIds;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "생성일시", example = "2024-01-01 12:00:00")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "수정일시", example = "2024-01-01 12:00:00")
    private LocalDateTime updatedAt;
}