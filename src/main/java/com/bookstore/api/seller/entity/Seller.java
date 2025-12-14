package com.bookstore.api.seller.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "sellers",
        indexes = {
                @Index(name = "idx_seller_business_number", columnList = "business_number"),
                @Index(name = "idx_seller_email", columnList = "email")
        }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("판매자 ID")
    private Long id;

    @Column(name = "business_name", nullable = false, length = 255)
    @Comment("사업자 상호명")
    private String businessName;

    @Column(name = "business_number", nullable = false, unique = true, length = 20)
    @Comment("사업자 등록번호")
    private String businessNumber;

    @Column(nullable = false, unique = true, length = 255)
    @Comment("이메일")
    private String email;

    @Column(name = "phone_number", nullable = false, length = 20)
    @Comment("전화번호")
    private String phoneNumber;

    @Column(nullable = false, length = 255)
    @Comment("주소")
    private String address;

    @Column(name = "payout_bank", nullable = false, length = 100)
    @Comment("정산 은행")
    private String payoutBank;

    @Column(name = "payout_account", nullable = false, length = 100)
    @Comment("정산 계좌번호")
    private String payoutAccount;

    @Column(name = "payout_holder", nullable = false, length = 100)
    @Comment("예금주")
    private String payoutHolder;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    @Comment("생성 시각")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    @Comment("수정 시각")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    @Comment("삭제 시각 (소프트 삭제)")
    private LocalDateTime deletedAt;

    @Column(name = "is_active", nullable = false)
    @Comment("활성화 여부")
    @Builder.Default
    private Boolean isActive = true;

    /**
     * 판매자 정보 수정
     */
    public void update(String businessName, String email, String phoneNumber,
                       String address, String payoutBank, String payoutAccount, String payoutHolder) {
        if (businessName != null) this.businessName = businessName;
        if (email != null) this.email = email;
        if (phoneNumber != null) this.phoneNumber = phoneNumber;
        if (address != null) this.address = address;
        if (payoutBank != null) this.payoutBank = payoutBank;
        if (payoutAccount != null) this.payoutAccount = payoutAccount;
        if (payoutHolder != null) this.payoutHolder = payoutHolder;
    }

    /**
     * 판매자 삭제 (소프트 삭제)
     */
    public void delete() {
        this.deletedAt = LocalDateTime.now();
        this.isActive = false;
    }

    /**
     * 판매자 활성화
     */
    public void activate() {
        this.isActive = true;
    }

    /**
     * 판매자 비활성화
     */
    public void deactivate() {
        this.isActive = false;
    }
}