package com.bookstore.api.seller.repository;

import com.bookstore.api.seller.entity.Seller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {

    /**
     * 사업자 등록번호로 조회 (삭제되지 않은 판매자만)
     */
    Optional<Seller> findByBusinessNumberAndDeletedAtIsNull(String businessNumber);

    /**
     * 이메일로 조회 (삭제되지 않은 판매자만)
     */
    Optional<Seller> findByEmailAndDeletedAtIsNull(String email);

    /**
     * 사업자 등록번호 중복 체크 (삭제되지 않은 판매자만)
     */
    boolean existsByBusinessNumberAndDeletedAtIsNull(String businessNumber);

    /**
     * 이메일 중복 체크 (삭제되지 않은 판매자만)
     */
    boolean existsByEmailAndDeletedAtIsNull(String email);

    /**
     * ID로 판매자 조회 (삭제되지 않은 판매자만)
     */
    @Query("SELECT s FROM Seller s WHERE s.id = :id AND s.deletedAt IS NULL")
    Optional<Seller> findByIdAndNotDeleted(@Param("id") Long id);

    /**
     * 활성 판매자 목록 조회 (페이징)
     */
    @Query("SELECT s FROM Seller s WHERE s.deletedAt IS NULL AND s.isActive = true")
    Page<Seller> findAllActiveSellers(Pageable pageable);

    /**
     * 전체 판매자 목록 조회 (삭제되지 않은 판매자만, 페이징)
     */
    @Query("SELECT s FROM Seller s WHERE s.deletedAt IS NULL")
    Page<Seller> findAllNotDeleted(Pageable pageable);

    /**
     * 사업자 상호명으로 검색 (삭제되지 않은 판매자만)
     */
    @Query("SELECT s FROM Seller s WHERE s.businessName LIKE %:keyword% AND s.deletedAt IS NULL")
    Page<Seller> searchByBusinessName(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 이메일로 검색 (삭제되지 않은 판매자만)
     */
    @Query("SELECT s FROM Seller s WHERE s.email LIKE %:keyword% AND s.deletedAt IS NULL")
    Page<Seller> searchByEmail(@Param("keyword") String keyword, Pageable pageable);
}