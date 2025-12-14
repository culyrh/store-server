package com.bookstore.api.seller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "판매자 수정 요청")
public class UpdateSellerRequest {

    @Schema(description = "사업자 상호명", example = "책방서점")
    @Size(max = 255, message = "사업자 상호명은 255자 이하여야 합니다.")
    private String businessName;

    @Schema(description = "이메일", example = "seller@example.com")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @Schema(description = "전화번호", example = "02-1234-5678")
    @Size(max = 20, message = "전화번호는 20자 이하여야 합니다.")
    private String phoneNumber;

    @Schema(description = "주소", example = "서울특별시 강남구")
    @Size(max = 255, message = "주소는 255자 이하여야 합니다.")
    private String address;

    @Schema(description = "정산 은행", example = "국민은행")
    @Size(max = 100, message = "정산 은행은 100자 이하여야 합니다.")
    private String payoutBank;

    @Schema(description = "정산 계좌번호", example = "123-456-789012")
    @Size(max = 100, message = "정산 계좌번호는 100자 이하여야 합니다.")
    private String payoutAccount;

    @Schema(description = "예금주", example = "홍길동")
    @Size(max = 100, message = "예금주는 100자 이하여야 합니다.")
    private String payoutHolder;
}