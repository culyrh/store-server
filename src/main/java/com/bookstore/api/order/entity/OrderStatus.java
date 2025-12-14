package com.bookstore.api.order.entity;

public enum OrderStatus {
    CREATED,      // 생성
    PAID,         // 결제완료
    SHIPPED,      // 배송중
    DELIVERED,    // 배송완료
    CANCELED      // 취소
}