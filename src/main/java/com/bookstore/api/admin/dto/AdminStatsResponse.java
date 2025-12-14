package com.bookstore.api.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "관리자 통계 응답")
public class AdminStatsResponse {

    @Schema(description = "총 사용자 수", example = "1000")
    private Long totalUsers;

    @Schema(description = "총 도서 수", example = "500")
    private Long totalBooks;

    @Schema(description = "총 주문 수", example = "2500")
    private Long totalOrders;

    @Schema(description = "총 매출액", example = "50000000")
    private BigDecimal totalRevenue;

    @Schema(description = "오늘 신규 사용자", example = "15")
    private Long todayNewUsers;

    @Schema(description = "오늘 주문 수", example = "87")
    private Long todayOrders;

    @Schema(description = "오늘 매출액", example = "1234500")
    private BigDecimal todayRevenue;
}