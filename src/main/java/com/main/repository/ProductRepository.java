package com.main.repository;

import com.main.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository  extends JpaRepository<Product, String> {
    // Nếu có cả danh mục cha và con
    @Query("SELECT p FROM Product p WHERE p.category.categoryId = :childCategoryId AND p.category.parent.categoryId = :parentCategoryId")
    Page<Product> findByParentAndChildCategory(
            @Param("parentCategoryId") String parentId,
            @Param("childCategoryId") String childId,
            Pageable pageable
    );

    // Nếu chỉ có danh mục cha
    @Query("SELECT p FROM Product p WHERE p.category.parent.categoryId = :parentCategoryId")
    Page<Product> findByParentCategoryOnly(
            @Param("parentCategoryId") String parentId,
            Pageable pageable
    );

    @Query("SELECT p FROM Product p WHERE p.category.parent.categoryId = :parentCategoryId")
    List<Product> findByParentCategoryOnly2(
            @Param("parentCategoryId") String parentId
    );
}
