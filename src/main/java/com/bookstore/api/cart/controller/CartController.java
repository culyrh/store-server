package com.bookstore.api.cart.controller;

import com.bookstore.api.cart.dto.*;
import com.bookstore.api.cart.service.CartService;
import com.bookstore.api.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "Cart", description = "장바구니 API")
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @Operation(summary = "장바구니 추가")
    @PostMapping
    public ResponseEntity<ApiResponse<CartResponse>> addToCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AddToCartRequest request) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("장바구니에 추가되었습니다", cartService.addToCart(userId, request)));
    }

    @Operation(summary = "내 장바구니 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<List<CartResponse>>> getMyCart(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(cartService.getMyCart(userId)));
    }

    @Operation(summary = "수량 수정")
    @PutMapping("/{cartId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateCartQuantity(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long cartId,
            @Valid @RequestBody UpdateCartRequest request) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("수량이 수정되었습니다",
                cartService.updateCartQuantity(userId, cartId, request)));
    }

    @Operation(summary = "장바구니 삭제")
    @DeleteMapping("/{cartId}")
    public ResponseEntity<ApiResponse<Void>> removeFromCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long cartId) {
        Long userId = Long.parseLong(userDetails.getUsername());
        cartService.removeFromCart(userId, cartId);
        return ResponseEntity.ok(ApiResponse.<Void>success("삭제되었습니다"));
    }

    @Operation(summary = "장바구니 비우기")
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        cartService.clearCart(userId);
        return ResponseEntity.ok(ApiResponse.<Void>success("장바구니가 비워졌습니다"));
    }

    @Operation(summary = "장바구니 개수")
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> getCartCount(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(cartService.getCartCount(userId)));
    }
}