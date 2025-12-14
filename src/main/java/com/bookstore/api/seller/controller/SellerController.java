package com.bookstore.api.seller.controller;

import com.bookstore.api.common.dto.ApiResponse;
import com.bookstore.api.common.dto.PageResponse;
import com.bookstore.api.seller.dto.CreateSellerRequest;
import com.bookstore.api.seller.dto.SellerResponse;
import com.bookstore.api.seller.dto.UpdateSellerRequest;
import com.bookstore.api.seller.service.SellerService;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/sellers")
@RequiredArgsConstructor
@Tag(name = "판매자", description = "판매자 관련 API")
public class SellerController {

    private final SellerService sellerService;

    @Operation(summary = "판매자 생성", description = "새로운 판매자를 등록합니다. (관리자 전용)")
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
                    description = "사업자 등록번호 또는 이메일 중복",
                    content = @Content(schema = @Schema(implementation = com.bookstore.api.common.dto.ErrorResponse.class))
            )
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<SellerResponse> createSeller(@Valid @RequestBody CreateSellerRequest request) {
        log.info("판매자 생성 요청: {}", request.getBusinessName());
        SellerResponse response = sellerService.createSeller(request);
        return ApiResponse.success("판매자가 생성되었습니다.", response);
    }

    @Operation(summary = "판매자 조회", description = "판매자 ID로 판매자를 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "판매자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = com.bookstore.api.common.dto.ErrorResponse.class))
            )
    })
    @GetMapping("/{sellerId}")
    public ApiResponse<SellerResponse> getSeller(
            @Parameter(description = "판매자 ID", example = "1")
            @PathVariable Long sellerId
    ) {
        log.info("판매자 조회: sellerId={}", sellerId);
        SellerResponse response = sellerService.getSeller(sellerId);
        return ApiResponse.success(response);
    }

    @Operation(summary = "전체 판매자 목록 조회", description = "전체 판매자 목록을 페이징하여 조회합니다.")
    @GetMapping
    public ApiResponse<PageResponse<SellerResponse>> getAllSellers(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        log.info("전체 판매자 목록 조회");
        PageResponse<SellerResponse> response = sellerService.getAllSellers(pageable);
        return ApiResponse.success(response);
    }

    @Operation(summary = "활성 판매자 목록 조회", description = "활성화된 판매자 목록을 조회합니다.")
    @GetMapping("/active")
    public ApiResponse<PageResponse<SellerResponse>> getActiveSellers(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        log.info("활성 판매자 목록 조회");
        PageResponse<SellerResponse> response = sellerService.getActiveSellers(pageable);
        return ApiResponse.success(response);
    }

    @Operation(summary = "판매자 검색 (상호명)", description = "상호명으로 판매자를 검색합니다.")
    @GetMapping("/search/business-name")
    public ApiResponse<PageResponse<SellerResponse>> searchSellersByBusinessName(
            @Parameter(description = "검색 키워드", example = "책방")
            @RequestParam String keyword,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        log.info("판매자 검색 (상호명): {}", keyword);
        PageResponse<SellerResponse> response = sellerService.searchSellersByBusinessName(keyword, pageable);
        return ApiResponse.success(response);
    }

    @Operation(summary = "판매자 검색 (이메일)", description = "이메일로 판매자를 검색합니다.")
    @GetMapping("/search/email")
    public ApiResponse<PageResponse<SellerResponse>> searchSellersByEmail(
            @Parameter(description = "검색 키워드", example = "seller@example.com")
            @RequestParam String keyword,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        log.info("판매자 검색 (이메일): {}", keyword);
        PageResponse<SellerResponse> response = sellerService.searchSellersByEmail(keyword, pageable);
        return ApiResponse.success(response);
    }

    @Operation(summary = "판매자 수정", description = "판매자 정보를 수정합니다. (관리자 전용)")
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
                    description = "판매자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = com.bookstore.api.common.dto.ErrorResponse.class))
            )
    })
    @PatchMapping("/{sellerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<SellerResponse> updateSeller(
            @Parameter(description = "판매자 ID", example = "1")
            @PathVariable Long sellerId,
            @Valid @RequestBody UpdateSellerRequest request
    ) {
        log.info("판매자 수정: sellerId={}", sellerId);
        SellerResponse response = sellerService.updateSeller(sellerId, request);
        return ApiResponse.success("판매자 정보가 수정되었습니다.", response);
    }

    @Operation(summary = "판매자 삭제", description = "판매자를 삭제합니다. (관리자 전용)")
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
                    description = "판매자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = com.bookstore.api.common.dto.ErrorResponse.class))
            )
    })
    @DeleteMapping("/{sellerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteSeller(
            @Parameter(description = "판매자 ID", example = "1")
            @PathVariable Long sellerId
    ) {
        log.info("판매자 삭제: sellerId={}", sellerId);
        sellerService.deleteSeller(sellerId);
        return ApiResponse.success("판매자가 삭제되었습니다.");
    }

    @Operation(summary = "판매자 활성화", description = "판매자를 활성화합니다. (관리자 전용)")
    @SecurityRequirement(name = "JWT")
    @PostMapping("/{sellerId}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<SellerResponse> activateSeller(
            @Parameter(description = "판매자 ID", example = "1")
            @PathVariable Long sellerId
    ) {
        log.info("판매자 활성화: sellerId={}", sellerId);
        SellerResponse response = sellerService.activateSeller(sellerId);
        return ApiResponse.success("판매자가 활성화되었습니다.", response);
    }

    @Operation(summary = "판매자 비활성화", description = "판매자를 비활성화합니다. (관리자 전용)")
    @SecurityRequirement(name = "JWT")
    @PostMapping("/{sellerId}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<SellerResponse> deactivateSeller(
            @Parameter(description = "판매자 ID", example = "1")
            @PathVariable Long sellerId
    ) {
        log.info("판매자 비활성화: sellerId={}", sellerId);
        SellerResponse response = sellerService.deactivateSeller(sellerId);
        return ApiResponse.success("판매자가 비활성화되었습니다.", response);
    }
}