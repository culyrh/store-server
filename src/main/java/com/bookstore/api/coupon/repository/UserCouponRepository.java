package com.bookstore.api.coupon.repository;

import com.bookstore.api.coupon.entity.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {

    List<UserCoupon> findByUserId(Long userId);

    List<UserCoupon> findByUserIdAndIsUsedFalse(Long userId);

    Optional<UserCoupon> findByUserIdAndCouponId(Long userId, Long couponId);

    boolean existsByUserIdAndCouponId(Long userId, Long couponId);
}