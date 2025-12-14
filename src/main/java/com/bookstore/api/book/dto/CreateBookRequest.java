package com.bookstore.api.book.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
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
@Schema(description = "도서 생성 요청")
public class CreateBookRequest {

    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 255, message = "제목은 255자 이내여야 합니다")
    @Schema(description = "도서 제목", example = "클린 코드")
    private String title;

    @NotBlank(message = "저자는 필수입니다")
    @Size(max = 100, message = "저자는 100자 이내여야 합니다")
    @Schema(description = "저자", example = "로버트 C. 마틴")
    private String author;

    @NotBlank(message = "출판사는 필수입니다")
    @Size(max = 100, message = "출판사는 100자 이내여야 합니다")
    @Schema(description = "출판사", example = "인사이트")
    private String publisher;

    @Schema(description = "도서 요약", example = "애자일 소프트웨어 장인 정신")
    private String summary;

    @NotBlank(message = "ISBN은 필수입니다")
    @Size(max = 20, message = "ISBN은 20자 이내여야 합니다")
    @Schema(description = "ISBN", example = "9788966260959")
    private String isbn;

    @NotNull(message = "가격은 필수입니다")
    @DecimalMin(value = "0.0", inclusive = false, message = "가격은 0보다 커야 합니다")
    @Schema(description = "가격", example = "33000")
    private BigDecimal price;

    @NotNull(message = "출판일은 필수입니다")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "출판일", example = "2013-12-24")
    private LocalDate publicationDate;

    @Schema(description = "판매자 ID", example = "1")
    private Long sellerId;

    @Schema(description = "카테고리 ID 목록", example = "[1, 2, 3]")
    private List<Long> categoryIds;
}