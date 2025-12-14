package com.bookstore.api.seller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "판매자 생성 요청")
public class CreateSellerRequest {

    @Schema(description = "사업자 상호명", example = "책방서점", required = true)
    @NotBlank(message = "사업자 상호명을 입력해주세요.")
    @Size(max = 255, message = "사업자 상호명은 255자 이하여야 합니다.")
    private String businessName;

    @Schema(description = "사업자 등록번호", example = "123-45-67890", required = true)
    @NotBlank(message = "사업자 등록번호를 입력해주세요.")
    @Pattern(regexp = "^\\d{3}-\\d{2}-\\d{5}$", message = "올바른 사업자 등록번호 형식이 아닙니다. (예: 123-45-67890)")
    private String businessNumber;

    @Schema(description = "이메일", example = "seller@example.com", required = true)
    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @Schema(description = "전화번호", example = "02-1234-5678", required = true)
    @NotBlank(message = "전화번호를 입력해주세요.")
    @Size(max = 20, message = "전화번호는 20자 이하여야 합니다.")
    private String phoneNumber;

    @Schema(description = "주소", example = "서울특별시 강남구", required = true)
    @NotBlank(message = "주소를 입력해주세요.")
    @Size(max = 255, message = "주소는 255자 이하여야 합니다.")
    private String address;

    @Schema(description = "정산 은행", example = "국민은행", required = true)
    @NotBlank(message = "정산 은행을 입력해주세요.")
    @Size(max = 100, message = "정산 은행은 100자 이하여야 합니다.")
    private String payoutBank;

    @Schema(description = "정산 계좌번호", example = "123-456-789012", required = true)
    @NotBlank(message = "정산 계좌번호를 입력해주세요.")
    @Size(max = 100, message = "정산 계좌번호는 100자 이하여야 합니다.")
    private String payoutAccount;

    @Schema(description = "예금주", example = "홍길동", required = true)
    @NotBlank(message = "예금주를 입력해주세요.")
    @Size(max = 100, message = "예금주는 100자 이하여야 합니다.")
    private String payoutHolder;
}