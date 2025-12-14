package com.bookstore.api.seller.dto;

import com.bookstore.api.seller.entity.Seller;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "판매자 응답")
public class SellerResponse {

    @Schema(description = "판매자 ID", example = "1")
    private Long id;

    @Schema(description = "사업자 상호명", example = "책방서점")
    private String businessName;

    @Schema(description = "사업자 등록번호", example = "123-45-67890")
    private String businessNumber;

    @Schema(description = "이메일", example = "seller@example.com")
    private String email;

    @Schema(description = "전화번호", example = "02-1234-5678")
    private String phoneNumber;

    @Schema(description = "주소", example = "서울특별시 강남구")
    private String address;

    @Schema(description = "정산 은행", example = "국민은행")
    private String payoutBank;

    @Schema(description = "정산 계좌번호", example = "123-456-789012")
    private String payoutAccount;

    @Schema(description = "예금주", example = "홍길동")
    private String payoutHolder;

    @Schema(description = "활성화 여부", example = "true")
    private Boolean isActive;

    @Schema(description = "생성 시각", example = "2025-12-14T10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "수정 시각", example = "2025-12-14T10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    public static SellerResponse from(Seller seller) {
        return SellerResponse.builder()
                .id(seller.getId())
                .businessName(seller.getBusinessName())
                .businessNumber(seller.getBusinessNumber())
                .email(seller.getEmail())
                .phoneNumber(seller.getPhoneNumber())
                .address(seller.getAddress())
                .payoutBank(seller.getPayoutBank())
                .payoutAccount(seller.getPayoutAccount())
                .payoutHolder(seller.getPayoutHolder())
                .isActive(seller.getIsActive())
                .createdAt(seller.getCreatedAt())
                .updatedAt(seller.getUpdatedAt())
                .build();
    }
}