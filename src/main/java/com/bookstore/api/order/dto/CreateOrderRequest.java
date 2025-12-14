package com.bookstore.api.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "주문 생성 요청")
public class CreateOrderRequest {

    @Schema(description = "장바구니 ID 목록 (장바구니에서 주문)")
    private List<Long> cartIds;

    @Schema(description = "직접 주문 항목")
    private List<OrderItemRequest> items;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "주문 항목")
    public static class OrderItemRequest {

        @Schema(description = "도서 ID", example = "1")
        private Long bookId;

        @Schema(description = "수량", example = "2")
        private Integer quantity;
    }
}