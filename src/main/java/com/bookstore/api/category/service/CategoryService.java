package com.bookstore.api.category.service;

import com.bookstore.api.category.dto.CategoryResponse;
import com.bookstore.api.category.dto.CreateCategoryRequest;
import com.bookstore.api.category.dto.UpdateCategoryRequest;
import com.bookstore.api.category.entity.Category;
import com.bookstore.api.category.repository.CategoryRepository;
import com.bookstore.api.common.exception.BusinessException;
import com.bookstore.api.common.exception.ErrorCode;
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
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * 카테고리 생성
     */
    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        log.info("카테고리 생성 시도: {}", request.getName());

        // 이름 중복 체크
        if (categoryRepository.existsByName(request.getName())) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE, "이미 존재하는 카테고리 이름입니다.");
        }

        // 상위 카테고리 조회
        Category parent = null;
        if (request.getParentId() != null) {
            parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
        }

        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .parent(parent)
                .build();

        Category savedCategory = categoryRepository.save(category);
        log.info("카테고리 생성 완료: categoryId={}", savedCategory.getId());

        return CategoryResponse.from(savedCategory);
    }

    /**
     * 카테고리 조회
     */
    public CategoryResponse getCategory(Long categoryId) {
        log.debug("카테고리 조회: categoryId={}", categoryId);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        return CategoryResponse.from(category);
    }

    /**
     * 전체 카테고리 목록 조회
     */
    public List<CategoryResponse> getAllCategories() {
        log.debug("전체 카테고리 목록 조회");

        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(CategoryResponse::fromWithoutChildren)
                .collect(Collectors.toList());
    }

    /**
     * 최상위 카테고리 목록 조회 (계층 구조)
     */
    public List<CategoryResponse> getRootCategories() {
        log.debug("최상위 카테고리 목록 조회");

        List<Category> rootCategories = categoryRepository.findAllRootCategories();
        return rootCategories.stream()
                .map(CategoryResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 하위 카테고리 목록 조회
     */
    public List<CategoryResponse> getSubCategories(Long parentId) {
        log.debug("하위 카테고리 조회: parentId={}", parentId);

        // 부모 카테고리 존재 확인
        categoryRepository.findById(parentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        List<Category> subCategories = categoryRepository.findByParentId(parentId);
        return subCategories.stream()
                .map(CategoryResponse::fromWithoutChildren)
                .collect(Collectors.toList());
    }

    /**
     * 카테고리 검색
     */
    public List<CategoryResponse> searchCategories(String keyword) {
        log.debug("카테고리 검색: keyword={}", keyword);

        List<Category> categories = categoryRepository.searchByName(keyword);
        return categories.stream()
                .map(CategoryResponse::fromWithoutChildren)
                .collect(Collectors.toList());
    }

    /**
     * 카테고리 수정
     */
    @Transactional
    public CategoryResponse updateCategory(Long categoryId, UpdateCategoryRequest request) {
        log.info("카테고리 수정 시도: categoryId={}", categoryId);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        // 이름 중복 체크 (자기 자신 제외)
        if (request.getName() != null && !request.getName().equals(category.getName())) {
            if (categoryRepository.existsByName(request.getName())) {
                throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE, "이미 존재하는 카테고리 이름입니다.");
            }
        }

        category.update(request.getName(), request.getDescription());

        log.info("카테고리 수정 완료: categoryId={}", categoryId);
        return CategoryResponse.from(category);
    }

    /**
     * 카테고리 삭제
     */
    @Transactional
    public void deleteCategory(Long categoryId) {
        log.info("카테고리 삭제 시도: categoryId={}", categoryId);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        // 하위 카테고리가 있는지 확인
        if (!category.getChildren().isEmpty()) {
            throw new BusinessException(
                    ErrorCode.STATE_CONFLICT,
                    "하위 카테고리가 존재하는 카테고리는 삭제할 수 없습니다."
            );
        }

        categoryRepository.delete(category);
        log.info("카테고리 삭제 완료: categoryId={}", categoryId);
    }
}
