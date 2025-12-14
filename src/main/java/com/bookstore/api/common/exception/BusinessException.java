package com.bookstore.api.common.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Map<String, String> details;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.details = null;
    }

    public BusinessException(ErrorCode errorCode, Map<String, String> details) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.details = details;
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.details = null;
    }

    public BusinessException(ErrorCode errorCode, String message, Map<String, String> details) {
        super(message);
        this.errorCode = errorCode;
        this.details = details;
    }
}