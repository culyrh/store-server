package com.bookstore.api.coupon.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "사용자 쿠폰 응답")
public class UserCouponResponse {

    @Schema(description = "사용자 쿠폰 ID", example = "1")
    private Long id;

    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "쿠폰 ID", example = "1")
    private Long couponId;

    @Schema(description = "할인율", example = "10.00")
    private BigDecimal discountRate;

    @Schema(description = "사용 여부", example = "false")
    private Boolean isUsed;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "사용일시", example = "2024-01-15 14:30:00")
    private LocalDateTime usedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "발급일시", example = "2024-01-01 12:00:00")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "쿠폰 시작일시", example = "2024-01-01 00:00:00")
    private LocalDateTime startAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "쿠폰 종료일시", example = "2024-12-31 23:59:59")
    private LocalDateTime endAt;
}