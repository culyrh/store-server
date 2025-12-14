package com.bookstore.api.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_user_email", columnList = "email"),
                @Index(name = "idx_user_role", columnList = "role"),
                @Index(name = "idx_user_created_at", columnList = "created_at")
        }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("사용자 ID")
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    @Comment("이메일")
    private String email;

    @Column(nullable = false, length = 255)
    @Comment("비밀번호 (암호화)")
    private String password;

    @Column(nullable = false, length = 100)
    @Comment("이름")
    private String name;

    @Column(name = "birth_date")
    @Comment("생년월일")
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    @Comment("성별")
    private Gender gender;

    @Column(length = 255)
    @Comment("주소")
    private String address;

    @Column(name = "phone_number", length = 20)
    @Comment("전화번호")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Comment("권한 (USER, ADMIN)")
    @Builder.Default
    private UserRole role = UserRole.USER;

    @Column(name = "profile_image", length = 255)
    @Comment("프로필 이미지 URL")
    private String profileImage;

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
     * 프로필 업데이트
     */
    public void updateProfile(String name, String address, String phoneNumber, String profileImage) {
        if (name != null) this.name = name;
        if (address != null) this.address = address;
        if (phoneNumber != null) this.phoneNumber = phoneNumber;
        if (profileImage != null) this.profileImage = profileImage;
    }

    /**
     * 비밀번호 변경
     */
    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    /**
     * 회원 탈퇴 (소프트 삭제)
     */
    public void delete() {
        this.deletedAt = LocalDateTime.now();
        this.isActive = false;
    }

    /**
     * 계정 비활성화
     */
    public void deactivate() {
        this.isActive = false;
    }

    /**
     * 계정 활성화
     */
    public void activate() {
        this.isActive = true;
    }
}