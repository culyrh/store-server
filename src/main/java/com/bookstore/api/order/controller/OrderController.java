package com.bookstore.api.order.controller;

import com.bookstore.api.common.dto.ApiResponse;
import com.bookstore.api.common.dto.PageResponse;
import com.bookstore.api.order.dto.CreateOrderRequest;
import com.bookstore.api.order.dto.OrderResponse;
import com.bookstore.api.order.entity.OrderStatus;
import com.bookstore.api.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Order", description = "주문 관리 API")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "주문 생성", description = "장바구니 또는 직접 주문을 생성합니다")
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateOrderRequest request) {
        Long userId = Long.parseLong(userDetails.getUsername());
        OrderResponse response = orderService.createOrder(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("주문이 생성되었습니다", response));
    }

    @Operation(summary = "주문 조회", description = "주문 ID로 주문을 조회합니다")
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "주문 ID") @PathVariable Long orderId) {
        Long userId = Long.parseLong(userDetails.getUsername());
        OrderResponse response = orderService.getOrder(userId, orderId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "내 주문 목록 조회", description = "내 주문 목록을 페이징하여 조회합니다")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getMyOrders(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Long userId = Long.parseLong(userDetails.getUsername());
        Page<OrderResponse> page = orderService.getMyOrders(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(page)));
    }

    @Operation(summary = "주문 상태 변경", description = "주문 상태를 변경합니다 (ADMIN 권한 필요)")
    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @Parameter(description = "주문 ID") @PathVariable Long orderId,
            @Parameter(description = "변경할 상태") @RequestParam OrderStatus status) {
        OrderResponse response = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(ApiResponse.success("주문 상태가 변경되었습니다", response));
    }

    @Operation(summary = "주문 취소", description = "주문을 취소합니다")
    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Void>> cancelOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "주문 ID") @PathVariable Long orderId) {
        Long userId = Long.parseLong(userDetails.getUsername());
        orderService.cancelOrder(userId, orderId);
        return ResponseEntity.ok(ApiResponse.<Void>success("주문이 취소되었습니다"));
    }
}