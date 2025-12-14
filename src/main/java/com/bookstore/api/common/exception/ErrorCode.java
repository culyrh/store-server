package com.bookstore.api.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 400 Bad Request
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "잘못된 요청입니다."),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", "입력값 검증에 실패했습니다."),
    INVALID_QUERY_PARAM(HttpStatus.BAD_REQUEST, "INVALID_QUERY_PARAM", "쿼리 파라미터가 올바르지 않습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "INVALID_INPUT_VALUE", "입력값이 올바르지 않습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "INVALID_PASSWORD", "비밀번호가 일치하지 않습니다."),

    // 401 Unauthorized
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "인증이 필요합니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "TOKEN_EXPIRED", "토큰이 만료되었습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", "유효하지 않은 토큰입니다."),
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "TOKEN_NOT_FOUND", "토큰이 존재하지 않습니다."),

    // 403 Forbidden
    FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN", "접근 권한이 없습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "해당 리소스에 접근할 수 없습니다."),

    // 404 Not Found
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", "요청한 리소스를 찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
    BOOK_NOT_FOUND(HttpStatus.NOT_FOUND, "BOOK_NOT_FOUND", "도서를 찾을 수 없습니다."),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "CATEGORY_NOT_FOUND", "카테고리를 찾을 수 없습니다."),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "ORDER_NOT_FOUND", "주문을 찾을 수 없습니다."),
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW_NOT_FOUND", "리뷰를 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMENT_NOT_FOUND", "댓글을 찾을 수 없습니다."),
    SELLER_NOT_FOUND(HttpStatus.NOT_FOUND, "SELLER_NOT_FOUND", "판매자를 찾을 수 없습니다."),
    COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "COUPON_NOT_FOUND", "쿠폰을 찾을 수 없습니다."),
    CART_NOT_FOUND(HttpStatus.NOT_FOUND, "CART_NOT_FOUND", "장바구니 항목을 찾을 수 없습니다."),

    // 409 Conflict
    DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "DUPLICATE_RESOURCE", "이미 존재하는 리소스입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "DUPLICATE_EMAIL", "이미 사용 중인 이메일입니다."),
    DUPLICATE_ISBN(HttpStatus.CONFLICT, "DUPLICATE_ISBN", "이미 등록된 ISBN입니다."),
    DUPLICATE_REVIEW(HttpStatus.CONFLICT, "DUPLICATE_REVIEW", "이미 리뷰를 작성했습니다."),
    STATE_CONFLICT(HttpStatus.CONFLICT, "STATE_CONFLICT", "리소스 상태가 충돌합니다."),
    ALREADY_DELETED(HttpStatus.CONFLICT, "ALREADY_DELETED", "이미 삭제된 리소스입니다."),

    // 422 Unprocessable Entity
    UNPROCESSABLE_ENTITY(HttpStatus.UNPROCESSABLE_ENTITY, "UNPROCESSABLE_ENTITY", "처리할 수 없는 요청입니다."),
    INVALID_ORDER_STATUS(HttpStatus.UNPROCESSABLE_ENTITY, "INVALID_ORDER_STATUS", "유효하지 않은 주문 상태입니다."),
    OUT_OF_STOCK(HttpStatus.UNPROCESSABLE_ENTITY, "OUT_OF_STOCK", "재고가 부족합니다."),
    COUPON_EXPIRED(HttpStatus.UNPROCESSABLE_ENTITY, "COUPON_EXPIRED", "쿠폰이 만료되었습니다."),
    COUPON_ALREADY_USED(HttpStatus.UNPROCESSABLE_ENTITY, "COUPON_ALREADY_USED", "이미 사용된 쿠폰입니다."),

    // 429 Too Many Requests
    TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS, "TOO_MANY_REQUESTS", "요청 한도를 초과했습니다."),
    RATE_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "RATE_LIMIT_EXCEEDED", "요청 속도 제한을 초과했습니다."),

    // 500 Internal Server Error
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다."),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "DATABASE_ERROR", "데이터베이스 오류가 발생했습니다."),
    UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "UNKNOWN_ERROR", "알 수 없는 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}