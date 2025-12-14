package com.bookstore.api.admin.service;

import com.bookstore.api.admin.dto.AdminStatsResponse;
import com.bookstore.api.book.repository.BookRepository;
import com.bookstore.api.order.repository.OrderRepository;
import com.bookstore.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final OrderRepository orderRepository;

    /**
     * 대시보드 통계 조회
     */
    public AdminStatsResponse getDashboardStats() {
        long totalUsers = userRepository.count();
        long totalBooks = bookRepository.count();
        long totalOrders = orderRepository.count();

        // TODO: 실제 매출 계산 로직 구현 필요
        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal todayRevenue = BigDecimal.ZERO;

        return AdminStatsResponse.builder()
                .totalUsers(totalUsers)
                .totalBooks(totalBooks)
                .totalOrders(totalOrders)
                .totalRevenue(totalRevenue)
                .todayNewUsers(0L)  // TODO: 오늘 신규 사용자 계산
                .todayOrders(0L)    // TODO: 오늘 주문 수 계산
                .todayRevenue(todayRevenue)
                .build();
    }
}