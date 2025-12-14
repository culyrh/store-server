-- ============================================
-- 1. 사용자 관리
-- ============================================
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       name VARCHAR(100) NOT NULL,
                       birth_date DATE,
                       gender VARCHAR(10),
                       address VARCHAR(255),
                       phone_number VARCHAR(20),
                       role VARCHAR(20) NOT NULL DEFAULT 'USER',
                       profile_image VARCHAR(255),
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       deleted_at TIMESTAMP,
                       is_active BOOLEAN NOT NULL DEFAULT TRUE
);

-- ============================================
-- 2. 판매자 관리
-- ============================================
CREATE TABLE sellers (
                         id BIGSERIAL PRIMARY KEY,
                         business_name VARCHAR(255) NOT NULL,
                         business_number VARCHAR(20) UNIQUE NOT NULL,
                         email VARCHAR(255) UNIQUE NOT NULL,
                         phone_number VARCHAR(20) NOT NULL,
                         address VARCHAR(255) NOT NULL,
                         payout_bank VARCHAR(100) NOT NULL,
                         payout_account VARCHAR(100) NOT NULL,
                         payout_holder VARCHAR(100) NOT NULL,
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         deleted_at TIMESTAMP,
                         is_active BOOLEAN NOT NULL DEFAULT TRUE
);

-- ============================================
-- 3. 카테고리 관리
-- ============================================
CREATE TABLE categories (
                            id BIGSERIAL PRIMARY KEY,
                            name VARCHAR(100) UNIQUE NOT NULL,
                            description TEXT,
                            parent_id BIGINT,
                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            FOREIGN KEY (parent_id) REFERENCES categories(id) ON DELETE SET NULL
);

-- ============================================
-- 4. 도서 관리
-- ============================================
CREATE TABLE books (
                       id BIGSERIAL PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       author VARCHAR(100) NOT NULL,
                       publisher VARCHAR(100) NOT NULL,
                       summary TEXT,
                       isbn VARCHAR(20) UNIQUE NOT NULL,
                       price DECIMAL(10,2) NOT NULL,
                       publication_date DATE NOT NULL,
                       seller_id BIGINT,
                       categories TEXT,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       deleted_at TIMESTAMP,
                       FOREIGN KEY (seller_id) REFERENCES sellers(id) ON DELETE SET NULL
);

CREATE INDEX idx_book_title ON books(title);
CREATE INDEX idx_book_author ON books(author);
CREATE INDEX idx_book_publisher ON books(publisher);
CREATE INDEX idx_book_isbn ON books(isbn);

-- ============================================
-- 5. 장바구니 관리
-- ============================================
CREATE TABLE carts (
                       id BIGSERIAL PRIMARY KEY,
                       user_id BIGINT NOT NULL,
                       book_id BIGINT NOT NULL,
                       quantity INT NOT NULL DEFAULT 1,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       deleted_at TIMESTAMP,
                       FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                       FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX idx_cart_user_book ON carts(user_id, book_id) WHERE deleted_at IS NULL;

-- ============================================
-- 6. 주문 관리
-- ============================================
CREATE TABLE orders (
                        id BIGSERIAL PRIMARY KEY,
                        user_id BIGINT NOT NULL,
                        status VARCHAR(20) NOT NULL DEFAULT 'CREATED',
                        total_amount DECIMAL(10,2) NOT NULL,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT
);

CREATE INDEX idx_order_user ON orders(user_id);
CREATE INDEX idx_order_status ON orders(status);
CREATE INDEX idx_order_created_at ON orders(created_at);

-- ============================================
-- 7. 주문 항목 관리
-- ============================================
CREATE TABLE order_items (
                             id BIGSERIAL PRIMARY KEY,
                             order_id BIGINT NOT NULL,
                             book_id BIGINT NOT NULL,
                             quantity INT NOT NULL,
                             price DECIMAL(10,2) NOT NULL,
                             status VARCHAR(20) NOT NULL DEFAULT 'CREATED',
                             created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
                             FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE RESTRICT
);

CREATE INDEX idx_order_item_order ON order_items(order_id);
CREATE INDEX idx_order_item_book ON order_items(book_id);

-- ============================================
-- 8. 리뷰 관리
-- ============================================
CREATE TABLE reviews (
                         id BIGSERIAL PRIMARY KEY,
                         user_id BIGINT NOT NULL,
                         book_id BIGINT NOT NULL,
                         rating INT NOT NULL,
                         comment TEXT NOT NULL,
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         deleted_at TIMESTAMP,
                         FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                         FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX idx_review_user_book ON reviews(user_id, book_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_review_rating ON reviews(rating);
CREATE INDEX idx_review_created_at ON reviews(created_at);

-- ============================================
-- 9. 댓글 관리
-- ============================================
CREATE TABLE comments (
                          id BIGSERIAL PRIMARY KEY,
                          user_id BIGINT NOT NULL,
                          review_id BIGINT NOT NULL,
                          content TEXT NOT NULL,
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          deleted_at TIMESTAMP,
                          FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                          FOREIGN KEY (review_id) REFERENCES reviews(id) ON DELETE CASCADE
);

CREATE INDEX idx_comment_review ON comments(review_id);
CREATE INDEX idx_comment_user ON comments(user_id);

-- ============================================
-- 10. 좋아요 관리
-- ============================================
CREATE TABLE likes (
                       id BIGSERIAL PRIMARY KEY,
                       user_id BIGINT NOT NULL,
                       target_type VARCHAR(20) NOT NULL,
                       target_id BIGINT NOT NULL,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX idx_like_unique ON likes(user_id, target_type, target_id);

-- ============================================
-- 11. 즐겨찾기(위시리스트) 관리
-- ============================================
CREATE TABLE favorites (
                           id BIGSERIAL PRIMARY KEY,
                           user_id BIGINT NOT NULL,
                           book_id BIGINT NOT NULL,
                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           deleted_at TIMESTAMP,
                           FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                           FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX idx_favorite_user_book ON favorites(user_id, book_id) WHERE deleted_at IS NULL;

-- ============================================
-- 12. 도서 조회 기록
-- ============================================
CREATE TABLE book_views (
                            id BIGSERIAL PRIMARY KEY,
                            user_id BIGINT NOT NULL,
                            book_id BIGINT NOT NULL,
                            view_count INT NOT NULL DEFAULT 1,
                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                            FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX idx_book_view_user_book ON book_views(user_id, book_id);
CREATE INDEX idx_book_view_book ON book_views(book_id);

-- ============================================
-- 13. 할인 관리
-- ============================================
CREATE TABLE discounts (
                           id BIGSERIAL PRIMARY KEY,
                           book_id BIGINT NOT NULL,
                           discount_rate DECIMAL(5,2) NOT NULL,
                           start_at TIMESTAMP NOT NULL,
                           end_at TIMESTAMP NOT NULL,
                           is_valid BOOLEAN NOT NULL DEFAULT TRUE,
                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
);

CREATE INDEX idx_discount_book ON discounts(book_id);
CREATE INDEX idx_discount_is_valid ON discounts(is_valid);
CREATE INDEX idx_discount_start_at ON discounts(start_at);
CREATE INDEX idx_discount_end_at ON discounts(end_at);

-- ============================================
-- 14. 쿠폰 관리
-- ============================================
CREATE TABLE coupons (
                         id BIGSERIAL PRIMARY KEY,
                         discount_rate DECIMAL(5,2) NOT NULL,
                         start_at TIMESTAMP NOT NULL,
                         end_at TIMESTAMP NOT NULL,
                         is_valid BOOLEAN NOT NULL DEFAULT TRUE,
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_coupon_is_valid ON coupons(is_valid);
CREATE INDEX idx_coupon_start_at ON coupons(start_at);
CREATE INDEX idx_coupon_end_at ON coupons(end_at);

-- ============================================
-- 15. 사용자-쿠폰 관계
-- ============================================
CREATE TABLE user_coupons (
                              id BIGSERIAL PRIMARY KEY,
                              user_id BIGINT NOT NULL,
                              coupon_id BIGINT NOT NULL,
                              is_used BOOLEAN NOT NULL DEFAULT FALSE,
                              used_at TIMESTAMP,
                              created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                              FOREIGN KEY (coupon_id) REFERENCES coupons(id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX idx_user_coupon_unique ON user_coupons(user_id, coupon_id);
CREATE INDEX idx_user_coupon_user ON user_coupons(user_id);
CREATE INDEX idx_user_coupon_is_used ON user_coupons(is_used);

-- ============================================
-- 16. 정산 관리
-- ============================================
CREATE TABLE settlements (
                             id BIGSERIAL PRIMARY KEY,
                             seller_id BIGINT NOT NULL,
                             total_sales DECIMAL(12,2) NOT NULL,
                             commission DECIMAL(12,2) NOT NULL,
                             final_payout DECIMAL(12,2) NOT NULL,
                             period_start DATE NOT NULL,
                             period_end DATE NOT NULL,
                             settlement_date TIMESTAMP NOT NULL,
                             created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             FOREIGN KEY (seller_id) REFERENCES sellers(id) ON DELETE RESTRICT
);

CREATE INDEX idx_settlement_seller ON settlements(seller_id);
CREATE INDEX idx_settlement_period_start ON settlements(period_start);
CREATE INDEX idx_settlement_period_end ON settlements(period_end);

-- ============================================
-- 17. 정산 항목
-- ============================================
CREATE TABLE settlement_items (
                                  id BIGSERIAL PRIMARY KEY,
                                  settlement_id BIGINT NOT NULL,
                                  order_item_id BIGINT NOT NULL,
                                  item_amount DECIMAL(10,2) NOT NULL,
                                  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  FOREIGN KEY (settlement_id) REFERENCES settlements(id) ON DELETE CASCADE,
                                  FOREIGN KEY (order_item_id) REFERENCES order_items(id) ON DELETE RESTRICT
);

CREATE INDEX idx_settlement_item_settlement ON settlement_items(settlement_id);
CREATE INDEX idx_settlement_item_order_item ON settlement_items(order_item_id);

-- ============================================
-- 18. 도서 통계
-- ============================================
CREATE TABLE book_stats (
                            id BIGSERIAL PRIMARY KEY,
                            book_id BIGINT NOT NULL,
                            view_count INT NOT NULL DEFAULT 0,
                            purchase_count INT NOT NULL DEFAULT 0,
                            favorite_count INT NOT NULL DEFAULT 0,
                            favorite_cancel_count INT NOT NULL DEFAULT 0,
                            cart_delete_count INT NOT NULL DEFAULT 0,
                            favorite_then_purchase_count INT NOT NULL DEFAULT 0,
                            view_then_purchase_count INT NOT NULL DEFAULT 0,
                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX idx_book_stats_book ON book_stats(book_id);
CREATE INDEX idx_book_stats_view_count ON book_stats(view_count);
CREATE INDEX idx_book_stats_purchase_count ON book_stats(purchase_count);