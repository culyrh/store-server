package com.bookstore.api.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "페이지네이션 응답")
public class PageResponse<T> {

    @Schema(description = "데이터 목록")
    private List<T> content;

    @Schema(description = "현재 페이지 번호 (0부터 시작)", example = "0")
    private Integer page;

    @Schema(description = "페이지 크기", example = "20")
    private Integer size;

    @Schema(description = "전체 데이터 개수", example = "153")
    private Long totalElements;

    @Schema(description = "전체 페이지 수", example = "8")
    private Integer totalPages;

    @Schema(description = "정렬 정보", example = "createdAt,DESC")
    private String sort;

    @Schema(description = "첫 페이지 여부", example = "true")
    private Boolean isFirst;

    @Schema(description = "마지막 페이지 여부", example = "false")
    private Boolean isLast;

    // Page 객체로부터 PageResponse 생성
    public static <T> PageResponse<T> of(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .sort(page.getSort().toString())
                .isFirst(page.isFirst())
                .isLast(page.isLast())
                .build();
    }
}