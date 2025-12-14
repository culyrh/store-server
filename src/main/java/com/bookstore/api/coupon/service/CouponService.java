package com.bookstore.api.coupon.service;

import com.bookstore.api.common.exception.BusinessException;
import com.bookstore.api.common.exception.ErrorCode;
import com.bookstore.api.coupon.dto.CouponResponse;
import com.bookstore.api.coupon.dto.UserCouponResponse;
import com.bookstore.api.coupon.entity.Coupon;
import com.bookstore.api.coupon.entity.UserCoupon;
import com.bookstore.api.coupon.repository.CouponRepository;
import com.bookstore.api.coupon.repository.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponService {

    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;

    /**
     * 사용 가능한 쿠폰 목록 조회
     */
    public List<CouponResponse> getAvailableCoupons() {
        return couponRepository.findByIsValidTrue().stream()
                .map(this::convertToCouponResponse)
                .collect(Collectors.toList());
    }

    /**
     * 쿠폰 발급
     */
    @Transactional
    public void issueCoupon(Long userId, Long couponId) {
        // 중복 발급 체크
        if (userCouponRepository.existsByUserIdAndCouponId(userId, couponId)) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE, "이미 발급받은 쿠폰입니다");
        }

        // 쿠폰 존재 확인
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "쿠폰을 찾을 수 없습니다"));

        if (!coupon.getIsValid()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "유효하지 않은 쿠폰입니다");
        }

        UserCoupon userCoupon = UserCoupon.builder()
                .userId(userId)
                .couponId(couponId)
                .build();

        userCouponRepository.save(userCoupon);
        log.info("쿠폰 발급: userId={}, couponId={}", userId, couponId);
    }

    /**
     * 내 쿠폰 목록 조회
     */
    public List<UserCouponResponse> getMyCoupons(Long userId) {
        return userCouponRepository.findByUserId(userId).stream()
                .map(this::convertToUserCouponResponse)
                .collect(Collectors.toList());
    }

    /**
     * 내 사용 가능한 쿠폰 목록 조회
     */
    public List<UserCouponResponse> getMyAvailableCoupons(Long userId) {
        return userCouponRepository.findByUserIdAndIsUsedFalse(userId).stream()
                .map(this::convertToUserCouponResponse)
                .collect(Collectors.toList());
    }

    /**
     * 쿠폰 사용
     */
    @Transactional
    public void useCoupon(Long userId, Long userCouponId) {
        UserCoupon userCoupon = userCouponRepository.findById(userCouponId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "쿠폰을 찾을 수 없습니다"));

        // 본인 쿠폰 확인
        if (!userCoupon.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "본인의 쿠폰만 사용할 수 있습니다");
        }

        // 이미 사용한 쿠폰 확인
        if (userCoupon.getIsUsed()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "이미 사용된 쿠폰입니다");
        }

        userCoupon.use();
        log.info("쿠폰 사용: userCouponId={}", userCouponId);
    }

    /**
     * Coupon -> CouponResponse 변환
     */
    private CouponResponse convertToCouponResponse(Coupon coupon) {
        return CouponResponse.builder()
                .id(coupon.getId())
                .discountRate(coupon.getDiscountRate())
                .startAt(coupon.getStartAt())
                .endAt(coupon.getEndAt())
                .isValid(coupon.getIsValid())
                .build();
    }

    /**
     * UserCoupon -> UserCouponResponse 변환
     */
    private UserCouponResponse convertToUserCouponResponse(UserCoupon userCoupon) {
        Coupon coupon = couponRepository.findById(userCoupon.getCouponId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "쿠폰을 찾을 수 없습니다"));

        return UserCouponResponse.builder()
                .id(userCoupon.getId())
                .userId(userCoupon.getUserId())
                .couponId(userCoupon.getCouponId())
                .discountRate(coupon.getDiscountRate())
                .isUsed(userCoupon.getIsUsed())
                .usedAt(userCoupon.getUsedAt())
                .createdAt(userCoupon.getCreatedAt())
                .startAt(coupon.getStartAt())
                .endAt(coupon.getEndAt())
                .build();
    }
}