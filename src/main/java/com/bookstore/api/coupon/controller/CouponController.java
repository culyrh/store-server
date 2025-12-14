package com.bookstore.api.coupon.controller;

import com.bookstore.api.common.dto.ApiResponse;
import com.bookstore.api.coupon.dto.CouponResponse;
import com.bookstore.api.coupon.dto.UserCouponResponse;
import com.bookstore.api.coupon.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Coupon", description = "쿠폰 관리 API")
@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @Operation(summary = "사용 가능한 쿠폰 목록", description = "현재 사용 가능한 쿠폰 목록을 조회합니다")
    @GetMapping
    public ResponseEntity<ApiResponse<List<CouponResponse>>> getAvailableCoupons() {
        List<CouponResponse> coupons = couponService.getAvailableCoupons();
        return ResponseEntity.ok(ApiResponse.success(coupons));
    }

    @Operation(summary = "쿠폰 발급", description = "쿠폰을 발급받습니다")
    @PostMapping("/{couponId}/issue")
    public ResponseEntity<ApiResponse<Void>> issueCoupon(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "쿠폰 ID") @PathVariable Long couponId) {
        Long userId = Long.parseLong(userDetails.getUsername());
        couponService.issueCoupon(userId, couponId);
        return ResponseEntity.ok(ApiResponse.<Void>success("쿠폰이 발급되었습니다"));
    }

    @Operation(summary = "내 쿠폰 목록", description = "내가 발급받은 쿠폰 목록을 조회합니다")
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<UserCouponResponse>>> getMyCoupons(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        List<UserCouponResponse> coupons = couponService.getMyCoupons(userId);
        return ResponseEntity.ok(ApiResponse.success(coupons));
    }

    @Operation(summary = "내 사용 가능한 쿠폰 목록", description = "아직 사용하지 않은 쿠폰 목록을 조회합니다")
    @GetMapping("/my/available")
    public ResponseEntity<ApiResponse<List<UserCouponResponse>>> getMyAvailableCoupons(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        List<UserCouponResponse> coupons = couponService.getMyAvailableCoupons(userId);
        return ResponseEntity.ok(ApiResponse.success(coupons));
    }

    @Operation(summary = "쿠폰 사용", description = "쿠폰을 사용합니다")
    @PostMapping("/my/{userCouponId}/use")
    public ResponseEntity<ApiResponse<Void>> useCoupon(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "사용자 쿠폰 ID") @PathVariable Long userCouponId) {
        Long userId = Long.parseLong(userDetails.getUsername());
        couponService.useCoupon(userId, userCouponId);
        return ResponseEntity.ok(ApiResponse.<Void>success("쿠폰이 사용되었습니다"));
    }
}