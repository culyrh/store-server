package com.bookstore.api.user.repository;

import com.bookstore.api.user.entity.User;
import com.bookstore.api.user.entity.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 이메일로 사용자 조회 (삭제되지 않은 사용자만)
     */
    Optional<User> findByEmailAndDeletedAtIsNull(String email);

    /**
     * 이메일 중복 체크 (삭제되지 않은 사용자만)
     */
    boolean existsByEmailAndDeletedAtIsNull(String email);

    /**
     * ID로 사용자 조회 (삭제되지 않은 사용자만)
     */
    @Query("SELECT u FROM User u WHERE u.id = :id AND u.deletedAt IS NULL")
    Optional<User> findByIdAndNotDeleted(@Param("id") Long id);

    /**
     * 활성 사용자 목록 조회 (페이징)
     */
    @Query("SELECT u FROM User u WHERE u.deletedAt IS NULL AND u.isActive = true")
    Page<User> findAllActiveUsers(Pageable pageable);

    /**
     * 역할별 사용자 조회
     */
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.deletedAt IS NULL")
    Page<User> findByRole(@Param("role") UserRole role, Pageable pageable);

    /**
     * 이름으로 사용자 검색 (삭제되지 않은 사용자만)
     */
    @Query("SELECT u FROM User u WHERE u.name LIKE %:keyword% AND u.deletedAt IS NULL")
    Page<User> searchByName(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 이메일로 사용자 검색 (삭제되지 않은 사용자만)
     */
    @Query("SELECT u FROM User u WHERE u.email LIKE %:keyword% AND u.deletedAt IS NULL")
    Page<User> searchByEmail(@Param("keyword") String keyword, Pageable pageable);
}