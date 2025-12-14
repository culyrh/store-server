package com.bookstore.api.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "프로필 수정 요청")
public class UpdateProfileRequest {

    @Schema(description = "이름", example = "홍길동")
    @Size(min = 2, max = 100, message = "이름은 2자 이상 100자 이하여야 합니다.")
    private String name;

    @Schema(description = "주소", example = "서울특별시 서초구")
    @Size(max = 255, message = "주소는 255자 이하여야 합니다.")
    private String address;

    @Schema(description = "전화번호", example = "010-1234-5678")
    @Pattern(regexp = "^01[0-9]-?[0-9]{3,4}-?[0-9]{4}$", message = "올바른 전화번호 형식이 아닙니다.")
    private String phoneNumber;

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    @Size(max = 255, message = "프로필 이미지 URL은 255자 이하여야 합니다.")
    private String profileImage;
}