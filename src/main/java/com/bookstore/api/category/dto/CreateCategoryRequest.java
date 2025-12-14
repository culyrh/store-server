package com.bookstore.api.category.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "카테고리 생성 요청")
public class CreateCategoryRequest {

    @Schema(description = "카테고리 이름", example = "소설", required = true)
    @NotBlank(message = "카테고리 이름을 입력해주세요.")
    @Size(min = 1, max = 100, message = "카테고리 이름은 1자 이상 100자 이하여야 합니다.")
    private String name;

    @Schema(description = "카테고리 설명", example = "소설 카테고리입니다.")
    @Size(max = 500, message = "카테고리 설명은 500자 이하여야 합니다.")
    private String description;

    @Schema(description = "상위 카테고리 ID", example = "null")
    private Long parentId;
}
