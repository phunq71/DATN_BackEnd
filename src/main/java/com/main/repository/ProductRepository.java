package com.main.repository;

import com.main.entity.Category;
import com.main.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository  extends JpaRepository<Product, String> {
    //Tìm sp bang id
    Optional<Product> findByProductID(String id);
    List<Product> findByCategory(Category category);

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

    @Query("""
    SELECT COALESCE(pp.discountPercent, 0.0)
    FROM PromotionProduct pp
    JOIN pp.product p
    JOIN pp.promotion pr
    WHERE p.productID = :productID
      AND pr.startDate <= CURRENT_TIMESTAMP
      AND (pr.endDate IS NULL OR pr.endDate >= CURRENT_TIMESTAMP)
      AND pr.type = 'ProductDiscount'
    """)
    Byte findDiscountPercentByProductID(@Param("productID") String productID);
}
