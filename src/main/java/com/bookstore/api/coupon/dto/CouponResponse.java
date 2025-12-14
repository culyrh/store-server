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
@Schema(description = "쿠폰 응답")
public class CouponResponse {

    @Schema(description = "쿠폰 ID", example = "1")
    private Long id;

    @Schema(description = "할인율", example = "10.00")
    private BigDecimal discountRate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "시작일시", example = "2024-01-01 00:00:00")
    private LocalDateTime startAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "종료일시", example = "2024-12-31 23:59:59")
    private LocalDateTime endAt;

    @Schema(description = "유효 여부", example = "true")
    private Boolean isValid;
}