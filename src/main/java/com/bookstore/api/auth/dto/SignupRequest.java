package com.bookstore.api.auth.dto;

import com.bookstore.api.user.entity.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원가입 요청")
public class SignupRequest {

    @Schema(description = "이메일", example = "user@example.com", required = true)
    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @Schema(description = "비밀번호", example = "Password123!", required = true)
    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 8, max = 50, message = "비밀번호는 8자 이상 50자 이하여야 합니다.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "비밀번호는 대소문자, 숫자, 특수문자를 포함해야 합니다."
    )
    private String password;

    @Schema(description = "비밀번호 확인", example = "Password123!", required = true)
    @NotBlank(message = "비밀번호 확인을 입력해주세요.")
    private String passwordConfirm;

    @Schema(description = "이름", example = "홍길동", required = true)
    @NotBlank(message = "이름을 입력해주세요.")
    @Size(min = 2, max = 100, message = "이름은 2자 이상 100자 이하여야 합니다.")
    private String name;

    @Schema(description = "생년월일", example = "1990-01-01")
    private LocalDate birthDate;

    @Schema(description = "성별", example = "MALE")
    private Gender gender;

    @Schema(description = "주소", example = "서울특별시 강남구")
    @Size(max = 255, message = "주소는 255자 이하여야 합니다.")
    private String address;

    @Schema(description = "전화번호", example = "010-1234-5678")
    @Pattern(regexp = "^01[0-9]-?[0-9]{3,4}-?[0-9]{4}$", message = "올바른 전화번호 형식이 아닙니다.")
    private String phoneNumber;
}