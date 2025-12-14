package com.bookstore.api.favorite.controller;

import com.bookstore.api.common.dto.ApiResponse;
import com.bookstore.api.common.dto.PageResponse;
import com.bookstore.api.favorite.dto.FavoriteResponse;
import com.bookstore.api.favorite.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Favorite", description = "찜(위시리스트) 관리 API")
@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Operation(summary = "찜 추가", description = "도서를 찜 목록에 추가합니다")
    @PostMapping("/{bookId}")
    public ResponseEntity<ApiResponse<Void>> addFavorite(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "도서 ID") @PathVariable Long bookId) {
        Long userId = Long.parseLong(userDetails.getUsername());
        favoriteService.addFavorite(userId, bookId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<Void>success("찜 목록에 추가되었습니다"));
    }

    @Operation(summary = "찜 삭제", description = "찜 목록에서 도서를 삭제합니다")
    @DeleteMapping("/{bookId}")
    public ResponseEntity<ApiResponse<Void>> removeFavorite(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "도서 ID") @PathVariable Long bookId) {
        Long userId = Long.parseLong(userDetails.getUsername());
        favoriteService.removeFavorite(userId, bookId);
        return ResponseEntity.ok(ApiResponse.<Void>success("찜 목록에서 삭제되었습니다"));
    }

    @Operation(summary = "내 찜 목록 조회", description = "내 찜 목록을 페이징하여 조회합니다")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<FavoriteResponse>>> getMyFavorites(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Long userId = Long.parseLong(userDetails.getUsername());
        Page<FavoriteResponse> page = favoriteService.getMyFavorites(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(page)));
    }

    @Operation(summary = "찜 여부 확인", description = "특정 도서가 찜 목록에 있는지 확인합니다")
    @GetMapping("/{bookId}/check")
    public ResponseEntity<ApiResponse<Boolean>> isFavorite(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "도서 ID") @PathVariable Long bookId) {
        Long userId = Long.parseLong(userDetails.getUsername());
        boolean isFavorite = favoriteService.isFavorite(userId, bookId);
        return ResponseEntity.ok(ApiResponse.success(isFavorite));
    }
}