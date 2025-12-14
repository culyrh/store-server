package com.bookstore.api.order.dto;

import com.bookstore.api.order.entity.OrderStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "주문 응답")
public class OrderResponse {

    @Schema(description = "주문 ID", example = "1")
    private Long id;

    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "주문 상태", example = "CREATED")
    private OrderStatus status;

    @Schema(description = "총 주문 금액", example = "99000")
    private BigDecimal totalAmount;

    @Schema(description = "주문 항목 목록")
    private List<OrderItemResponse> items;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "주문 생성일시", example = "2024-01-01 12:00:00")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "주문 수정일시", example = "2024-01-01 12:00:00")
    private LocalDateTime updatedAt;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "주문 항목 응답")
    public static class OrderItemResponse {

        @Schema(description = "주문 항목 ID", example = "1")
        private Long id;

        @Schema(description = "도서 ID", example = "1")
        private Long bookId;

        @Schema(description = "도서 제목", example = "클린 코드")
        private String bookTitle;

        @Schema(description = "수량", example = "2")
        private Integer quantity;

        @Schema(description = "가격", example = "33000")
        private BigDecimal price;

        @Schema(description = "상태", example = "CREATED")
        private OrderStatus status;
    }
}