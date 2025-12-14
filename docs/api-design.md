# API 설계 문서

## 개요
본 문서는 온라인 서점(Bookstore) API의 설계 내역 및 과제1 대비 변경사항을 정리합니다.

---

## 과제1 대비 수정사항

### 1. 엔드포인트 변경
| 구분 | 과제1 설계 | 최종 구현 | 변경 사유         |
|------|-----------|----------|---------------|
| 인증 | `/auth/login` | `/api/auth/login` | REST API 일관성  |
| 사용자 | `/users/me` | `/api/users/me` | 동일            |
| 장바구니 | `/cart` | `/api/cart` | 동일            |

### 2. 응답 형식 표준화
**과제1 설계:**
```json
{
  "isSuccess": true,
  "message": "성공",
  "payload": { ... }
}
```

**최종 구현:**
```json
{
  "success": true,
  "message": "성공",
  "data": { ... }
}
```

### 3. 페이지네이션 응답 통일
**Spring Data JPA의 Page 객체 활용:**
```json
{
  "content": [...],
  "page": 0,
  "size": 10,
  "totalElements": 153,
  "totalPages": 16,
  "sort": "createdAt,DESC"
}
```

### 4. 에러 응답 형식
**최종 구현:**
```json
{
  "timestamp": "2025-12-14T10:00:00Z",
  "path": "/api/books/999",
  "status": 404,
  "code": "BOOK_NOT_FOUND",
  "message": "해당 도서를 찾을 수 없습니다.",
  "details": { "bookId": 999 }
}
```

---

## API 엔드포인트 목록 (총 35개)

### 1. 인증 (Auth) - 3개
| Method | Endpoint | 설명 | 인증 필요 |
|--------|----------|------|----------|
| POST | `/api/auth/signup` | 회원가입 | ❌ |
| POST | `/api/auth/login` | 로그인 | ❌ |
| POST | `/api/auth/refresh` | 토큰 갱신 | ❌ |

### 2. 사용자 (Users) - 4개
| Method | Endpoint | 설명 | 인증 필요 |
|--------|----------|------|----------|
| GET | `/api/users/me` | 내 정보 조회 | ✅ |
| PUT | `/api/users/me` | 프로필 수정 | ✅ |
| PUT | `/api/users/me/password` | 비밀번호 변경 | ✅ |
| DELETE | `/api/users/me` | 회원 탈퇴 | ✅ |

### 3. 도서 (Books) - 5개
| Method | Endpoint | 설명 | 인증 필요 |
|--------|----------|------|----------|
| GET | `/api/books` | 도서 목록 조회 (검색/필터/페이지네이션) | ❌ |
| GET | `/api/books/{id}` | 도서 상세 조회 | ❌ |
| POST | `/api/books` | 도서 등록 | ✅ (ADMIN) |
| PUT | `/api/books/{id}` | 도서 수정 | ✅ (ADMIN) |
| DELETE | `/api/books/{id}` | 도서 삭제 | ✅ (ADMIN) |

### 4. 카테고리 (Categories) - 4개
| Method | Endpoint | 설명 | 인증 필요 |
|--------|----------|------|----------|
| GET | `/api/categories` | 카테고리 목록 조회 | ❌ |
| POST | `/api/categories` | 카테고리 생성 | ✅ (ADMIN) |
| PUT | `/api/categories/{id}` | 카테고리 수정 | ✅ (ADMIN) |
| DELETE | `/api/categories/{id}` | 카테고리 삭제 | ✅ (ADMIN) |

### 5. 판매자 (Sellers) - 5개
| Method | Endpoint | 설명 | 인증 필요 |
|--------|----------|------|----------|
| GET | `/api/sellers` | 판매자 목록 조회 | ✅ (ADMIN) |
| GET | `/api/sellers/{id}` | 판매자 상세 조회 | ✅ (ADMIN) |
| POST | `/api/sellers` | 판매자 등록 | ✅ (ADMIN) |
| PUT | `/api/sellers/{id}` | 판매자 정보 수정 | ✅ (ADMIN) |
| DELETE | `/api/sellers/{id}` | 판매자 삭제 | ✅ (ADMIN) |

### 6. 장바구니 (Cart) - 4개
| Method | Endpoint | 설명 | 인증 필요 |
|--------|----------|------|----------|
| GET | `/api/cart` | 장바구니 조회 | ✅ |
| POST | `/api/cart` | 장바구니에 추가 | ✅ |
| PUT | `/api/cart/{id}` | 수량 변경 | ✅ |
| DELETE | `/api/cart/{id}` | 장바구니 항목 삭제 | ✅ |

### 7. 주문 (Orders) - 3개
| Method | Endpoint | 설명 | 인증 필요 |
|--------|----------|------|----------|
| POST | `/api/orders` | 주문 생성 | ✅ |
| GET | `/api/orders` | 내 주문 목록 조회 | ✅ |
| GET | `/api/orders/{id}` | 주문 상세 조회 | ✅ |

### 8. 리뷰 (Reviews) - 5개
| Method | Endpoint | 설명 | 인증 필요 |
|--------|----------|------|----------|
| POST | `/api/reviews` | 리뷰 작성 | ✅ |
| GET | `/api/books/{bookId}/reviews` | 도서별 리뷰 조회 | ❌ |
| GET | `/api/reviews/me` | 내가 작성한 리뷰 조회 | ✅ |
| PUT | `/api/reviews/{id}` | 리뷰 수정 | ✅ |
| DELETE | `/api/reviews/{id}` | 리뷰 삭제 | ✅ |

### 9. 댓글 (Comments) - 3개
| Method | Endpoint | 설명 | 인증 필요 |
|--------|----------|------|----------|
| POST | `/api/reviews/{reviewId}/comments` | 댓글 작성 | ✅ |
| PUT | `/api/comments/{id}` | 댓글 수정 | ✅ |
| DELETE | `/api/comments/{id}` | 댓글 삭제 | ✅ |

### 10. 위시리스트 (Favorites) - 3개
| Method | Endpoint | 설명 | 인증 필요 |
|--------|----------|------|----------|
| GET | `/api/favorites` | 위시리스트 조회 | ✅ |
| POST | `/api/favorites/{bookId}` | 위시리스트에 추가 | ✅ |
| DELETE | `/api/favorites/{bookId}` | 위시리스트에서 삭제 | ✅ |

### 11. 쿠폰 (Coupons) - 2개
| Method | Endpoint | 설명 | 인증 필요 |
|--------|----------|------|----------|
| GET | `/api/coupons` | 사용 가능한 쿠폰 조회 | ✅ |
| POST | `/api/coupons/{couponId}/issue` | 쿠폰 발급받기 | ✅ |

### 12. 관리자 (Admin) - 3개
| Method | Endpoint | 설명 | 인증 필요 |
|--------|----------|------|----------|
| GET | `/api/admin/stats` | 통계 조회 | ✅ (ADMIN) |
| GET | `/api/admin/users` | 전체 사용자 조회 | ✅ (ADMIN) |
| PUT | `/api/admin/users/{id}/deactivate` | 사용자 비활성화 | ✅ (ADMIN) |

---

## 인증/인가

### JWT 기반 인증
- **Access Token**: 24시간 유효
- **Refresh Token**: 7일 유효
- Header: `Authorization: Bearer {token}`

### Role 기반 권한
- `ROLE_USER`: 일반 사용자
- `ROLE_ADMIN`: 관리자

---

## 공통 응답 형식

### 성공 응답
```json
{
  "success": true,
  "message": "요청이 성공적으로 처리되었습니다.",
  "data": { ... }
}
```

### 에러 응답
```json
{
  "timestamp": "2025-12-14T10:00:00Z",
  "path": "/api/books/999",
  "status": 404,
  "code": "BOOK_NOT_FOUND",
  "message": "해당 도서를 찾을 수 없습니다.",
  "details": { "bookId": 999 }
}
```

### 페이지네이션 응답
```json
{
  "content": [...],
  "page": 0,
  "size": 10,
  "totalElements": 153,
  "totalPages": 16,
  "sort": "createdAt,DESC"
}
```

---

## 주요 에러 코드 (15종)

| HTTP Status | Error Code | 설명 |
|-------------|-----------|------|
| 400 | `INVALID_INPUT_VALUE` | 잘못된 입력값 |
| 400 | `VALIDATION_FAILED` | 검증 실패 |
| 401 | `UNAUTHORIZED` | 인증 필요 |
| 401 | `INVALID_TOKEN` | 유효하지 않은 토큰 |
| 401 | `EXPIRED_TOKEN` | 만료된 토큰 |
| 401 | `INVALID_PASSWORD` | 잘못된 비밀번호 |
| 403 | `FORBIDDEN` | 접근 권한 없음 |
| 404 | `USER_NOT_FOUND` | 사용자를 찾을 수 없음 |
| 404 | `BOOK_NOT_FOUND` | 도서를 찾을 수 없음 |
| 404 | `ORDER_NOT_FOUND` | 주문을 찾을 수 없음 |
| 409 | `DUPLICATE_EMAIL` | 이미 존재하는 이메일 |
| 409 | `DUPLICATE_ISBN` | 이미 존재하는 ISBN |
| 422 | `OUT_OF_STOCK` | 재고 부족 |
| 429 | `RATE_LIMIT_EXCEEDED` | 요청 제한 초과 |
| 500 | `INTERNAL_SERVER_ERROR` | 서버 내부 오류 |

---

## 정렬 옵션
- `createdAt,DESC` (최신순)
- `price,ASC` (가격 낮은순)
- `price,DESC` (가격 높은순)
- `title,ASC` (제목 오름차순)