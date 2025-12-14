package com.bookstore.api.category.controller;

import com.bookstore.api.category.dto.CategoryResponse;
import com.bookstore.api.category.dto.CreateCategoryRequest;
import com.bookstore.api.category.dto.UpdateCategoryRequest;
import com.bookstore.api.category.service.CategoryService;
import com.bookstore.api.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "카테고리", description = "카테고리 관련 API")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "카테고리 생성", description = "새로운 카테고리를 생성합니다. (관리자 전용)")
    @SecurityRequirement(name = "JWT")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "생성 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "입력값 검증 실패",
                    content = @Content(schema = @Schema(implementation = com.bookstore.api.common.dto.ErrorResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "권한 없음",
                    content = @Content(schema = @Schema(implementation = com.bookstore.api.common.dto.ErrorResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "카테고리 이름 중복",
                    content = @Content(schema = @Schema(implementation = com.bookstore.api.common.dto.ErrorResponse.class))
            )
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<CategoryResponse> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        log.info("카테고리 생성 요청: {}", request.getName());
        CategoryResponse response = categoryService.createCategory(request);
        return ApiResponse.success("카테고리가 생성되었습니다.", response);
    }

    @Operation(summary = "카테고리 조회", description = "카테고리 ID로 카테고리를 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "카테고리를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = com.bookstore.api.common.dto.ErrorResponse.class))
            )
    })
    @GetMapping("/{categoryId}")
    public ApiResponse<CategoryResponse> getCategory(
            @Parameter(description = "카테고리 ID", example = "1")
            @PathVariable Long categoryId
    ) {
        log.info("카테고리 조회: categoryId={}", categoryId);
        CategoryResponse response = categoryService.getCategory(categoryId);
        return ApiResponse.success(response);
    }

    @Operation(summary = "전체 카테고리 목록 조회", description = "모든 카테고리를 조회합니다.")
    @GetMapping
    public ApiResponse<List<CategoryResponse>> getAllCategories() {
        log.info("전체 카테고리 목록 조회");
        List<CategoryResponse> response = categoryService.getAllCategories();
        return ApiResponse.success(response);
    }

    @Operation(summary = "최상위 카테고리 목록 조회", description = "최상위 카테고리를 계층 구조로 조회합니다.")
    @GetMapping("/root")
    public ApiResponse<List<CategoryResponse>> getRootCategories() {
        log.info("최상위 카테고리 목록 조회");
        List<CategoryResponse> response = categoryService.getRootCategories();
        return ApiResponse.success(response);
    }

    @Operation(summary = "하위 카테고리 목록 조회", description = "특정 카테고리의 하위 카테고리를 조회합니다.")
    @GetMapping("/{parentId}/children")
    public ApiResponse<List<CategoryResponse>> getSubCategories(
            @Parameter(description = "상위 카테고리 ID", example = "1")
            @PathVariable Long parentId
    ) {
        log.info("하위 카테고리 조회: parentId={}", parentId);
        List<CategoryResponse> response = categoryService.getSubCategories(parentId);
        return ApiResponse.success(response);
    }

    @Operation(summary = "카테고리 검색", description = "카테고리 이름으로 검색합니다.")
    @GetMapping("/search")
    public ApiResponse<List<CategoryResponse>> searchCategories(
            @Parameter(description = "검색 키워드", example = "소설")
            @RequestParam String keyword
    ) {
        log.info("카테고리 검색: keyword={}", keyword);
        List<CategoryResponse> response = categoryService.searchCategories(keyword);
        return ApiResponse.success(response);
    }

    @Operation(summary = "카테고리 수정", description = "카테고리 정보를 수정합니다. (관리자 전용)")
    @SecurityRequirement(name = "JWT")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "수정 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "권한 없음",
                    content = @Content(schema = @Schema(implementation = com.bookstore.api.common.dto.ErrorResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "카테고리를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = com.bookstore.api.common.dto.ErrorResponse.class))
            )
    })
    @PatchMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<CategoryResponse> updateCategory(
            @Parameter(description = "카테고리 ID", example = "1")
            @PathVariable Long categoryId,
            @Valid @RequestBody UpdateCategoryRequest request
    ) {
        log.info("카테고리 수정: categoryId={}", categoryId);
        CategoryResponse response = categoryService.updateCategory(categoryId, request);
        return ApiResponse.success("카테고리가 수정되었습니다.", response);
    }

    @Operation(summary = "카테고리 삭제", description = "카테고리를 삭제합니다. (관리자 전용)")
    @SecurityRequirement(name = "JWT")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "삭제 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "권한 없음",
                    content = @Content(schema = @Schema(implementation = com.bookstore.api.common.dto.ErrorResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "카테고리를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = com.bookstore.api.common.dto.ErrorResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "하위 카테고리가 존재",
                    content = @Content(schema = @Schema(implementation = com.bookstore.api.common.dto.ErrorResponse.class))
            )
    })
    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteCategory(
            @Parameter(description = "카테고리 ID", example = "1")
            @PathVariable Long categoryId
    ) {
        log.info("카테고리 삭제: categoryId={}", categoryId);
        categoryService.deleteCategory(categoryId);
        return ApiResponse.success("카테고리가 삭제되었습니다.");
    }
}