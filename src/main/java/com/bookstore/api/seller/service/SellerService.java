package com.bookstore.api.seller.service;

import com.bookstore.api.common.dto.PageResponse;
import com.bookstore.api.common.exception.BusinessException;
import com.bookstore.api.common.exception.ErrorCode;
import com.bookstore.api.seller.dto.CreateSellerRequest;
import com.bookstore.api.seller.dto.SellerResponse;
import com.bookstore.api.seller.dto.UpdateSellerRequest;
import com.bookstore.api.seller.entity.Seller;
import com.bookstore.api.seller.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SellerService {

    private final SellerRepository sellerRepository;

    /**
     * 판매자 생성
     */
    @Transactional
    public SellerResponse createSeller(CreateSellerRequest request) {
        log.info("판매자 생성 시도: {}", request.getBusinessName());

        // 사업자 등록번호 중복 체크
        if (sellerRepository.existsByBusinessNumberAndDeletedAtIsNull(request.getBusinessNumber())) {
            throw new BusinessException(
                    ErrorCode.DUPLICATE_RESOURCE,
                    "이미 등록된 사업자 등록번호입니다.",
                    Map.of("businessNumber", request.getBusinessNumber())
            );
        }

        // 이메일 중복 체크
        if (sellerRepository.existsByEmailAndDeletedAtIsNull(request.getEmail())) {
            throw new BusinessException(
                    ErrorCode.DUPLICATE_EMAIL,
                    "이미 사용 중인 이메일입니다.",
                    Map.of("email", request.getEmail())
            );
        }

        Seller seller = Seller.builder()
                .businessName(request.getBusinessName())
                .businessNumber(request.getBusinessNumber())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .payoutBank(request.getPayoutBank())
                .payoutAccount(request.getPayoutAccount())
                .payoutHolder(request.getPayoutHolder())
                .build();

        Seller savedSeller = sellerRepository.save(seller);
        log.info("판매자 생성 완료: sellerId={}", savedSeller.getId());

        return SellerResponse.from(savedSeller);
    }

    /**
     * 판매자 조회
     */
    public SellerResponse getSeller(Long sellerId) {
        log.debug("판매자 조회: sellerId={}", sellerId);

        Seller seller = sellerRepository.findByIdAndNotDeleted(sellerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SELLER_NOT_FOUND));

        return SellerResponse.from(seller);
    }

    /**
     * 전체 판매자 목록 조회
     */
    public PageResponse<SellerResponse> getAllSellers(Pageable pageable) {
        log.debug("전체 판매자 목록 조회");

        Page<Seller> sellers = sellerRepository.findAllNotDeleted(pageable);
        Page<SellerResponse> responsePage = sellers.map(SellerResponse::from);

        return PageResponse.of(responsePage);
    }

    /**
     * 활성 판매자 목록 조회
     */
    public PageResponse<SellerResponse> getActiveSellers(Pageable pageable) {
        log.debug("활성 판매자 목록 조회");

        Page<Seller> sellers = sellerRepository.findAllActiveSellers(pageable);
        Page<SellerResponse> responsePage = sellers.map(SellerResponse::from);

        return PageResponse.of(responsePage);
    }

    /**
     * 판매자 검색 (상호명)
     */
    public PageResponse<SellerResponse> searchSellersByBusinessName(String keyword, Pageable pageable) {
        log.debug("판매자 검색 (상호명): {}", keyword);

        Page<Seller> sellers = sellerRepository.searchByBusinessName(keyword, pageable);
        Page<SellerResponse> responsePage = sellers.map(SellerResponse::from);

        return PageResponse.of(responsePage);
    }

    /**
     * 판매자 검색 (이메일)
     */
    public PageResponse<SellerResponse> searchSellersByEmail(String keyword, Pageable pageable) {
        log.debug("판매자 검색 (이메일): {}", keyword);

        Page<Seller> sellers = sellerRepository.searchByEmail(keyword, pageable);
        Page<SellerResponse> responsePage = sellers.map(SellerResponse::from);

        return PageResponse.of(responsePage);
    }

    /**
     * 판매자 정보 수정
     */
    @Transactional
    public SellerResponse updateSeller(Long sellerId, UpdateSellerRequest request) {
        log.info("판매자 수정 시도: sellerId={}", sellerId);

        Seller seller = sellerRepository.findByIdAndNotDeleted(sellerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SELLER_NOT_FOUND));

        // 이메일 중복 체크 (자기 자신 제외)
        if (request.getEmail() != null && !request.getEmail().equals(seller.getEmail())) {
            if (sellerRepository.existsByEmailAndDeletedAtIsNull(request.getEmail())) {
                throw new BusinessException(
                        ErrorCode.DUPLICATE_EMAIL,
                        "이미 사용 중인 이메일입니다.",
                        Map.of("email", request.getEmail())
                );
            }
        }

        seller.update(
                request.getBusinessName(),
                request.getEmail(),
                request.getPhoneNumber(),
                request.getAddress(),
                request.getPayoutBank(),
                request.getPayoutAccount(),
                request.getPayoutHolder()
        );

        log.info("판매자 수정 완료: sellerId={}", sellerId);
        return SellerResponse.from(seller);
    }

    /**
     * 판매자 삭제 (소프트 삭제)
     */
    @Transactional
    public void deleteSeller(Long sellerId) {
        log.info("판매자 삭제 시도: sellerId={}", sellerId);

        Seller seller = sellerRepository.findByIdAndNotDeleted(sellerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SELLER_NOT_FOUND));

        seller.delete();

        log.info("판매자 삭제 완료: sellerId={}", sellerId);
    }

    /**
     * 판매자 활성화
     */
    @Transactional
    public SellerResponse activateSeller(Long sellerId) {
        log.info("판매자 활성화 시도: sellerId={}", sellerId);

        Seller seller = sellerRepository.findByIdAndNotDeleted(sellerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SELLER_NOT_FOUND));

        seller.activate();

        log.info("판매자 활성화 완료: sellerId={}", sellerId);
        return SellerResponse.from(seller);
    }

    /**
     * 판매자 비활성화
     */
    @Transactional
    public SellerResponse deactivateSeller(Long sellerId) {
        log.info("판매자 비활성화 시도: sellerId={}", sellerId);

        Seller seller = sellerRepository.findByIdAndNotDeleted(sellerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SELLER_NOT_FOUND));

        seller.deactivate();

        log.info("판매자 비활성화 완료: sellerId={}", sellerId);
        return SellerResponse.from(seller);
    }
}