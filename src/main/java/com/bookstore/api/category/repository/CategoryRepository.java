package com.bookstore.api.category.repository;

import com.bookstore.api.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * 이름으로 카테고리 조회
     */
    Optional<Category> findByName(String name);

    /**
     * 이름 중복 체크
     */
    boolean existsByName(String name);

    /**
     * 최상위 카테고리 목록 조회 (parent_id가 null인 것)
     */
    @Query("SELECT c FROM Category c WHERE c.parent IS NULL ORDER BY c.name ASC")
    List<Category> findAllRootCategories();

    /**
     * 특정 부모 카테고리의 하위 카테고리 조회
     */
    @Query("SELECT c FROM Category c WHERE c.parent.id = :parentId ORDER BY c.name ASC")
    List<Category> findByParentId(@Param("parentId") Long parentId);

    /**
     * 카테고리 이름으로 검색
     */
    @Query("SELECT c FROM Category c WHERE c.name LIKE %:keyword% ORDER BY c.name ASC")
    List<Category> searchByName(@Param("keyword") String keyword);
}