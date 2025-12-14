package com.bookstore.api.common.exception;

import com.bookstore.api.common.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * BusinessException 처리
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException e,
            WebRequest request
    ) {
        log.error("Business Exception: {}", e.getMessage(), e);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .status(e.getErrorCode().getStatus().value())
                .code(e.getErrorCode().getCode())
                .message(e.getMessage())
                .details(e.getDetails())
                .build();

        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(errorResponse);
    }

    /**
     * Validation 예외 처리 (@Valid, @Validated)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException e,
            WebRequest request
    ) {
        log.error("Validation Exception: {}", e.getMessage(), e);

        Map<String, String> details = new HashMap<>();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            details.put(error.getField(), error.getDefaultMessage());
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .status(HttpStatus.BAD_REQUEST.value())
                .code(ErrorCode.VALIDATION_FAILED.getCode())
                .message("입력값 검증에 실패했습니다.")
                .details(details)
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    /**
     * BindException 처리 (Form 데이터 바인딩 오류)
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(
            BindException e,
            WebRequest request
    ) {
        log.error("Bind Exception: {}", e.getMessage(), e);

        Map<String, String> details = new HashMap<>();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            details.put(error.getField(), error.getDefaultMessage());
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .status(HttpStatus.BAD_REQUEST.value())
                .code(ErrorCode.VALIDATION_FAILED.getCode())
                .message("입력값 검증에 실패했습니다.")
                .details(details)
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    /**
     * AccessDeniedException 처리 (권한 오류)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException e,
            WebRequest request
    ) {
        log.error("Access Denied Exception: {}", e.getMessage(), e);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .status(HttpStatus.FORBIDDEN.value())
                .code(ErrorCode.FORBIDDEN.getCode())
                .message("접근 권한이 없습니다.")
                .build();

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(errorResponse);
    }

    /**
     * BadCredentialsException 처리 (인증 실패)
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(
            BadCredentialsException e,
            WebRequest request
    ) {
        log.error("Bad Credentials Exception: {}", e.getMessage(), e);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .status(HttpStatus.UNAUTHORIZED.value())
                .code(ErrorCode.INVALID_PASSWORD.getCode())
                .message("이메일 또는 비밀번호가 일치하지 않습니다.")
                .build();

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(errorResponse);
    }

    /**
     * IllegalArgumentException 처리
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException e,
            WebRequest request
    ) {
        log.error("Illegal Argument Exception: {}", e.getMessage(), e);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .status(HttpStatus.BAD_REQUEST.value())
                .code(ErrorCode.INVALID_INPUT_VALUE.getCode())
                .message(e.getMessage())
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    /**
     * 모든 예외 처리 (최종 fallback)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(
            Exception e,
            WebRequest request
    ) {
        log.error("Unhandled Exception: {}", e.getMessage(), e);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .code(ErrorCode.INTERNAL_SERVER_ERROR.getCode())
                .message("서버 내부 오류가 발생했습니다.")
                .build();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }
}