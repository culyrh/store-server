# Bookstore Server

<br>

| 구분           | 주소                                               |
|--------------| ------------------------------------------------ |
| 배포 URL       | http://113.198.66.75:10217                       |
| Swagger      | http://113.198.66.75:10217/swagger-ui/index.html |
| Health Check | http://113.198.66.75:10217/actuator/health            |


<br>

---
## 1. 프로젝트 개요

이 프로젝트는 **과제 1에서 설계한 데이터베이스 스키마와 REST API 설계**를 기반으로, 이를 실제로 구현하고 **JCloud 환경에 배포**하는 것을 목표로 합니다.

온라인 서점(Bookstore)을 도메인으로 하여, 사용자 인증/인가(JWT), 상품·카테고리·주문 등 핵심 기능을 중심으로 **확장 가능한 API 서버 구조**를 설계했습니다.

<br>

### 핵심 목표

* DB 설계 → API 설계 → 실제 구현까지의 **백엔드 개발 경험**
* JWT 기반 인증/인가 및 Role 기반 접근 제어
* 페이지네이션/검색/정렬이 가능한 API
* Swagger / Postman 기반 문서화 및 테스트 자동화
* JCloud 실서버 배포 

<br>

## 2. 실행 방법

### 2-1. 로컬 실행

#### 전제조건
- Java 17 이상
- PostgreSQL 16 실행 중

#### 실행 단계
```bash
# 1. 환경 변수 설정
cp .env

# 2. 빌드
./gradlew clean build

# 3. 실행
java -jar build/libs/bookstore-api-0.0.1-SNAPSHOT.jar
```

또는 개발 환경에서 바로 실행:
```bash
./gradlew bootRun
```

### 2-2. Docker Compose로 전체 실행

모든 의존성(PostgreSQL, 애플리케이션)을 한 번에 실행:

```bash
# 빌드 및 실행
docker-compose up -d --build

# 종료
docker-compose down
````

<br>

---

## 3. 환경 변수 설명

.env.example 기준

```env
# Database
DB_HOST=postgres
DB_PORT=5432
DB_NAME=bookstore
DB_USERNAME={your_username}
DB_PASSWORD={your_password}

# Redis  
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=

# JWT
JWT_SECRET=your-super-secret-jwt-key-change-this-in-production-min-256-bits-long
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000

# Server
SERVER_PORT=8080
```

**`.env` 파일 및 실제 비밀 정보는 GitHub에 커밋하지 않았습니다.**

<br>

## 4. 배포 주소 (jcloud)

| 구분           | 주소                                               |
|--------------| ------------------------------------------------ |
| 배포 URL       | http://113.198.66.75:10217                       |
| Swagger      | http://113.198.66.75:10217/swagger-ui/index.html |
| Health Check | http://113.198.66.75:10217/actuator/health            |

<br>

---

## 5. 인증 / 인가 구조

### 5-1. 인증 흐름 (JWT)

1. `POST /auth/login`
2. Access Token / Refresh Token 발급
3. Access Token 만료 시 `POST /auth/refresh`
4. Authorization Header 사용

```http
Authorization: Bearer <ACCESS_TOKEN>
```

### 5-2. Role 기반 인가 (RBAC)

| Role       | 설명     |
| ---------- | ------ |
| ROLE_USER  | 일반 사용자 |
| ROLE_ADMIN | 관리자    |

관리자 전용 API 예시:

* 사용자 비활성화
* 전체 사용자 조회
* 통계 API

<br>

---

## 6. 예제 계정

| 구분    | 이메일                                           | 비밀번호      |
| ----- | --------------------------------------------- |-----------|
| USER  | [user1@example.com](mailto:user1@example.com) | P@ssw0rd! |
| ADMIN | [admin@example.com](mailto:admin@example.com) | P@ssw0rd!|

- /docs에서 Swagger 회원가입, 로그인 테스트 결과를 확인할 수 있습니다.

<br>

---

## 7. DB 연결 정보 (테스트용)

| 항목       | 값         |
|----------|-----------|
| Host     | postgres  |
| Port     | 5432      |
| DB_NAME   | bookstore |
| USERNAME | postgres  |

<br>

## 8. API 엔드포인트 요약

### Auth

* `POST /auth/login`
* `POST /auth/refresh`
* `POST /auth/logout`

### User

* `POST /users`
* `GET /users/me`
* `PATCH /users/me`
* `GET /users` (ADMIN)

### Category

* `POST /categories` (ADMIN)
* `GET /categories`
* `PATCH /categories/{id}` (ADMIN)
* `DELETE /categories/{id}` (ADMIN)

> 전체 엔드포인트는 **Swagger 문서** 참고

<br>

---

## 9. 보안 및 성능 고려사항

* JWT 기반 인증/인가
* 비밀번호 bcrypt 해시
* 인덱스 기반 조회 최적화
* Global Exception Handler로 에러 규격 통일
* 헬스체크 API 제공

<br>

## 10. 한계와 개선 계획

* 대용량 트래픽 환경에 대한 부하 테스트 미흡
* 관리자 통계 API 기능 확장 필요
* 캐싱 전략 세분화 예정

<br>

---

## 11. 공통 응답 & 에러 처리

### 성공 응답

```json
{
  "success": true,
  "data": { }
}
```

### 에러 응답

```json
{
  "timestamp": "2025-03-05T12:34:56Z",
  "path": "/api/posts/1",
  "status": 400,
  "code": "VALIDATION_FAILED",
  "message": "요청 값이 올바르지 않습니다.",
  "details": { }
}
```

<br>

## 12. 디렉토리 구조

```text
src/main/java/com/bookstore/api
├─ admin           # 관리자 전용 기능
├─ auth            # 인증/인가 (JWT, 로그인, 토큰 재발급)
├─ book            # 도서(상품)
├─ cart            # 장바구니
├─ category        # 카테고리 관리
├─ comment         # 댓글
├─ common          # 공통 응답/예외/유틸
├─ config          # 보안, Swagger 설정
├─ coupon          # 쿠폰
├─ favorite        # 즐겨찾기
├─ user            # 사용자 관리
├─ order           # 주문
├─ review          # 리뷰
├─ security        # Spring Security + JWT
├─ seller          # 판매자 관리
├─ healthcheck     # 헬스체크
└─ root            # 루트 엔드포인트
```

<br>

## 13. 기술 스택

| 구분      | 기술                           |
|---------|------------------------------|
| Language | Java 17                      |
| Framework | Spring Boot                  |
| Database | PostgreSQL                   |
| ORM     | Spring Data JPA (Hibernate)  |
| Auth    | JWT (Access / Refresh Token) |
| Migration | Flyway                       |
| API Docs | Swagger (springdoc-openapi)  |
| Test    | JUnit 5                      |
| Deploy  | JCloud + Docker              |
| CI      | Github Action                |
