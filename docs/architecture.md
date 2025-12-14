# 시스템 아키텍처

## 개요
온라인 서점(Bookstore) API 서버의 전체 아키텍처 및 설계 원칙

---

## 전체 아키텍처

```
┌─────────────────────────────────────────────────────────┐
│                      Client Layer                        │
│  (Postman, Swagger UI, Mobile App, Web Frontend)        │
└────────────────────┬────────────────────────────────────┘
                     │ HTTP/REST
┌────────────────────▼────────────────────────────────────┐
│                 Spring Boot Application                  │
│ ┌─────────────────────────────────────────────────────┐ │
│ │              Security Layer (JWT)                   │ │
│ │  - JwtAuthenticationFilter                          │ │
│ │  - JwtTokenProvider                                 │ │
│ │  - SecurityConfig                                   │ │
│ └─────────────────────────────────────────────────────┘ │
│                                                           │
│ ┌─────────────────────────────────────────────────────┐ │
│ │           Controller Layer (Presentation)           │ │
│ │  - AuthController                                   │ │
│ │  - UserController, BookController, etc.             │ │
│ │  - GlobalExceptionHandler                           │ │
│ └─────────────────────────────────────────────────────┘ │
│                          │                               │
│ ┌─────────────────────────────────────────────────────┐ │
│ │              Service Layer (Business)               │ │
│ │  - AuthService, UserService, BookService, etc.      │ │
│ │  - Business Logic & Validation                      │ │
│ └─────────────────────────────────────────────────────┘ │
│                          │                               │
│ ┌─────────────────────────────────────────────────────┐ │
│ │          Repository Layer (Data Access)             │ │
│ │  - UserRepository, BookRepository, etc.             │ │
│ │  - Spring Data JPA                                  │ │
│ └─────────────────────────────────────────────────────┘ │
└────────────────────┬────────────────────────────────────┘
                     │
        ┌────────────┴─────────────┐
        │                          │
┌───────▼────────┐      ┌─────────▼──────┐
│   PostgreSQL   │      │     Redis      │
│   Database     │      │     Cache      │
└────────────────┘      └────────────────┘
```

---

## 계층별 구조

### 1. Controller (Presentation Layer)
**역할**: HTTP 요청/응답 처리, 입력 검증

**위치**: `src/main/java/com/bookstore/api/{domain}/controller/`

**주요 책임**:
- REST API 엔드포인트 정의
- Request DTO → Service 전달
- Response DTO ← Service 수신
- HTTP 상태 코드 설정
- 입력 검증 (`@Valid`, `@Validated`)

**예시**:
```java
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;
    
    @GetMapping
    public ResponseEntity<PageResponse<BookResponse>> getBooks(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(bookService.getBooks(page, size));
    }
}
```

---

### 2. Service (Business Layer)
**역할**: 비즈니스 로직 처리, 트랜잭션 관리

**위치**: `src/main/java/com/bookstore/api/{domain}/service/`

**주요 책임**:
- 핵심 비즈니스 로직 구현
- 여러 Repository 조합
- 트랜잭션 경계 설정 (`@Transactional`)
- 도메인 규칙 검증
- Entity ↔ DTO 변환

**예시**:
```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;
    
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        // 비즈니스 로직
        // 재고 확인, 쿠폰 적용, 주문 생성 등
    }
}
```

---

### 3. Repository (Data Access Layer)
**역할**: 데이터베이스 접근

**위치**: `src/main/java/com/bookstore/api/{domain}/repository/`

**주요 책임**:
- CRUD 연산
- 커스텀 쿼리 정의 (`@Query`)
- Spring Data JPA 활용

**예시**:
```java
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Page<Book> findByTitleContaining(String title, Pageable pageable);
    
    @Query("SELECT b FROM Book b WHERE b.author = :author")
    List<Book> findByAuthor(@Param("author") String author);
}
```

---

### 4. Entity (Domain Layer)
**역할**: 도메인 모델 정의

**위치**: `src/main/java/com/bookstore/api/{domain}/entity/`

**주요 책임**:
- 데이터베이스 테이블 매핑
- 엔티티 관계 정의

**예시**:
```java
@Entity
@Table(name = "books")
@Getter @Setter
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    private String author;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private Seller seller;
}
```

---

## 패키지 구조

```
src/main/java/com/bookstore/api/
├── auth/                          # 인증 모듈
│   ├── controller/
│   │   └── AuthController.java
│   ├── dto/
│   │   ├── LoginRequest.java
│   │   └── TokenResponse.java
│   └── service/
│       └── AuthService.java
│
├── user/                          # 사용자 모듈
│   ├── controller/
│   ├── dto/
│   ├── entity/
│   ├── repository/
│   └── service/
│
├── book/                          # 도서 모듈
│   ├── controller/
│   ├── dto/
│   ├── entity/
│   ├── repository/
│   └── service/
│
├── order/                         # 주문 모듈
├── review/                        # 리뷰 모듈
├── cart/                          # 장바구니 모듈
├── category/                      # 카테고리 모듈
├── favorite/                      # 위시리스트 모듈
├── coupon/                        # 쿠폰 모듈
├── seller/                        # 판매자 모듈
├── comment/                       # 댓글 모듈
│
├── common/                        # 공통 모듈
│   ├── dto/                       # 공통 DTO
│   │   ├── ApiResponse.java
│   │   ├── ErrorResponse.java
│   │   └── PageResponse.java
│   ├── exception/                 # 예외 처리
│   │   ├── BusinessException.java
│   │   ├── ErrorCode.java
│   │   └── GlobalExceptionHandler.java
│   └── util/                      # 유틸리티
│       ├── DateTimeUtil.java
│       └── StringUtil.java
│
├── config/                        # 설정
│   ├── SecurityConfig.java
│   ├── SwaggerConfig.java
│   ├── RedisConfig.java
│   └── CacheConfig.java
│
├── security/                      # 보안
│   ├── jwt/
│   │   ├── JwtTokenProvider.java
│   │   ├── JwtAuthenticationFilter.java
│   │   ├── JwtAuthenticationEntryPoint.java
│   │   └── JwtAccessDeniedHandler.java
│   └── CustomUserDetailsService.java
│
├── admin/                         # 관리자 모듈
│   ├── controller/
│   └── service/
│
└── BookstoreApiApplication.java   # Main
```

---

## 보안 아키텍처

### JWT 인증 플로우
```
1. 사용자 로그인 (POST /api/auth/login)
   ↓
2. AuthService: 이메일/비밀번호 검증
   ↓
3. JwtTokenProvider: Access Token + Refresh Token 발급
   ↓
4. Client: Token 저장 (LocalStorage, Cookie, etc.)
   ↓
5. 이후 모든 요청에 Header 포함
   Authorization: Bearer {accessToken}
   ↓
6. JwtAuthenticationFilter: Token 검증
   ↓
7. SecurityContextHolder: 인증 정보 저장
   ↓
8. Controller: @PreAuthorize로 권한 검증
```

### Spring Security Filter Chain
```
DisableEncodeUrlFilter
↓
WebAsyncManagerIntegrationFilter
↓
SecurityContextHolderFilter
↓
HeaderWriterFilter
↓
CorsFilter
↓
LogoutFilter
↓
JwtAuthenticationFilter  ← 커스텀 필터
↓
RequestCacheAwareFilter
↓
SecurityContextHolderAwareRequestFilter
↓
AnonymousAuthenticationFilter
↓
SessionManagementFilter
↓
ExceptionTranslationFilter
↓
AuthorizationFilter
```

---

## 데이터 플로우

### 조회 요청 (Read)
```
Client
  ↓ GET /api/books?page=0&size=10
Controller (BookController)
  ↓ validation
Service (BookService)
  ↓ business logic
Repository (BookRepository)
  ↓ JPA Query
PostgreSQL
  ↑ Result Set
Repository
  ↑ List<Book>
Service
  ↑ Entity → DTO 변환
  ↑ PageResponse<BookResponse>
Controller
  ↑ ResponseEntity<PageResponse>
Client
```

### 생성 요청 (Create)
```
Client
  ↓ POST /api/orders + CreateOrderRequest
Controller (OrderController)
  ↓ @Valid validation
Service (OrderService)
  ↓ @Transactional
  ├─ 1. 재고 확인 (BookService)
  ├─ 2. 쿠폰 검증 (CouponService)
  ├─ 3. 주문 생성 (OrderRepository)
  └─ 4. 주문 항목 생성 (OrderItemRepository)
PostgreSQL (COMMIT)
  ↑
Service
  ↑ OrderResponse
Controller
  ↑ ResponseEntity<OrderResponse> (201 CREATED)
Client
```

---

## 예외 처리 전략

### GlobalExceptionHandler
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    // 비즈니스 예외
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        return ResponseEntity
            .status(e.getErrorCode().getStatus())
            .body(ErrorResponse.from(e));
    }
    
    // 입력 검증 예외
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(...) {
        // 검증 오류 처리
    }
    
    // 모든 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception e) {
        // 500 Internal Server Error
    }
}
```

---

## ⚡ 성능 최적화

### 1. Caching (Redis)
- **캐시 대상**: 카테고리 목록, 인기 도서
- **TTL**: 1시간 ~ 24시간
- **전략**: Cache-Aside Pattern

```java
@Cacheable(value = "categories", key = "'all'")
public List<CategoryResponse> getAllCategories() {
    return categoryRepository.findAll();
}
```

### 2. N+1 문제 해결
```java
// EntityGraph 사용
@EntityGraph(attributePaths = {"seller", "reviews"})
Optional<Book> findById(Long id);

// Fetch Join
@Query("SELECT b FROM Book b JOIN FETCH b.seller WHERE b.id = :id")
Optional<Book> findByIdWithSeller(@Param("id") Long id);
```

### 3. Pagination
- 모든 목록 조회는 Pageable 사용
- 기본 크기: 10, 최대 크기: 100

---

## 설정 관리

### Profile 기반 설정
```
application.yml              # 공통 설정
application-dev.yml          # 개발 환경
application-prod.yml         # 프로덕션 환경
```

### 환경 변수 (.env)
```env
DB_HOST=postgres
DB_PORT=5432
JWT_SECRET=your-super-secret-jwt-key-change-this-in-production-min-256-bits-long
```

---

## 모니터링 & 로깅

### Actuator Endpoints
- `/actuator/health` - 헬스체크

### 로깅 전략
```java
@Slf4j
public class BookService {
    public void createBook(CreateBookRequest request) {
        log.info("Creating book: {}", request.getTitle());
        // ...
        log.debug("Book created with ID: {}", book.getId());
    }
}
```

**로그 레벨**:
- `ERROR`: 에러 발생 (알림 필요)
- `WARN`: 경고 (잠재적 문제)
- `INFO`: 중요 이벤트 (생성, 수정, 삭제)
- `DEBUG`: 상세 디버깅 정보

---

## 🐳 배포 아키텍처 (Docker)

```
┌──────────────────────────────────────┐
│         Docker Compose               │
│                                      │
│  ┌────────────────────────────────┐ │
│  │  bookstore-api (Spring Boot)   │ │
│  │  Port: 8080                    │ │
│  └────────────────────────────────┘ │
│                │                     │
│  ┌─────────────┴──────────────────┐ │
│  │                                │ │
│  ▼                                ▼ │
│  ┌──────────────┐   ┌────────────┐ │
│  │  PostgreSQL  │   │ Redis(선택) │ │
│  │  Port: 5432  │   │ Port: 6379 │ │
│  └──────────────┘   └────────────┘ │
└──────────────────────────────────────┘
         │
         │ Port Forwarding (JCloud)
         ▼
   113.198.66.75:10217
```

---

## CI/CD Pipeline (GitHub Actions)

```
GitHub Push
  ↓
GitHub Actions
  ├─ 1. Checkout Code
  ├─ 2. Setup Java 17
  ├─ 3. Build with Gradle
  ├─ 4. Run Tests
  ├─ 5. Build Docker Image
  └─ 6. Push to Registry (Optional)
  
Manual Deployment (JCloud)
  ├─ 1. SSH to Server
  ├─ 2. git pull
  ├─ 3. docker-compose down
  ├─ 4. docker-compose up -d --build
  └─ 5. Verify Health Check
```
