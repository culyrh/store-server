package com.bookstore.api.admin.controller;

import com.bookstore.api.admin.dto.AdminStatsResponse;
import com.bookstore.api.admin.service.AdminService;
import com.bookstore.api.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "Admin", description = "관리자 API")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "대시보드 통계", description = "관리자 대시보드 통계를 조회합니다 (ADMIN 권한 필요)")
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<AdminStatsResponse>> getDashboardStats() {
        AdminStatsResponse stats = adminService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @Operation(summary = "도서 통계", description = "도서 관련 통계를 조회합니다 (ADMIN 권한 필요)")
    @GetMapping("/stats/books")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getBookStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("message", "Book Statistics - 추후 구현");
        stats.put("totalBooks", 0);
        stats.put("booksByCategory", new HashMap<>());
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @Operation(summary = "주문 통계", description = "주문 관련 통계를 조회합니다 (ADMIN 권한 필요)")
    @GetMapping("/stats/orders")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOrderStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("message", "Order Statistics - 추후 구현");
        stats.put("totalOrders", 0);
        stats.put("ordersByStatus", new HashMap<>());
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @Operation(summary = "사용자 통계", description = "사용자 관련 통계를 조회합니다 (ADMIN 권한 필요)")
    @GetMapping("/stats/users")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("message", "User Statistics - 추후 구현");
        stats.put("totalUsers", 0);
        stats.put("activeUsers", 0);
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}