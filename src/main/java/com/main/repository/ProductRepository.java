package com.main.repository;

import com.main.dto.ProductViewDTO;
import com.main.entity.Category;
import com.main.entity.Product;
import com.main.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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
    SELECT new com.main.dto.ProductViewDTO(p.productID, pp.discountPercent)
    FROM PromotionProduct pp
    JOIN pp.product p
    JOIN pp.promotion pr
    WHERE pr.startDate <= CURRENT_TIMESTAMP
      AND (pr.endDate IS NULL OR pr.endDate >= CURRENT_TIMESTAMP)
      AND pr.type = 'ProductDiscount'
""")
    List<ProductViewDTO> findDiscountedProducts();

    @Query(value = "SELECT * FROM Products WHERE createdDate >= DATEADD(DAY, -20, GETDATE())", nativeQuery = true)
    List<Product> findRecentProducts();


    @Query("""
    SELECT p
    FROM OrderDetail od
    JOIN od.item it
    JOIN it.variant va
    JOIN va.product p
    GROUP BY p
    ORDER BY SUM(od.quantity) DESC
""")
    List<Product> findTopSellingProducts(Pageable pageable);

    // lấy top sản phẩm được yêu thích nhất
    @Query(value = """
    SELECT p.* FROM products p
    JOIN (
        SELECT f.productId, COUNT(*) AS cnt,
               RANK() OVER (ORDER BY COUNT(*) DESC) AS rank
        FROM favorites f
        GROUP BY f.productId
    ) ranked ON p.productId = ranked.productId
    WHERE ranked.rank <= 12
    """, nativeQuery = true)
    List<Product> findTop12RankedProducts();


    @Query("""
    SELECT COALESCE(pp.discountPercent, 0.0)
    FROM PromotionProduct pp
    JOIN pp.product p
    JOIN pp.promotion pr
    WHERE p.productID = :productID
      AND pr.startDate <= CURRENT_TIMESTAMP
      AND (pr.endDate IS NULL OR pr.endDate >= CURRENT_TIMESTAMP)
      AND pr.type = 'ProductDiscount'
      AND pp.quantityUsed < pp.quantityRemaining
    """)
    Byte findDiscountPercentByProductID(@Param("productID") String productID);



    @Query(value = """
    SELECT CASE 
        WHEN COUNT(*) > 0 THEN 1 
        ELSE 0 
    END
    FROM Products
    WHERE productID = :productID
      AND createdDate >= CAST(DATEADD(DAY, -20, GETDATE()) AS DATE)
""", nativeQuery = true)
    int isNewProduct(@Param("productID") String productID);

    @Query("SELECT COUNT(od.orderDetailID) " +
            "FROM OrderDetail od " +
            "JOIN od.item it " +
            "JOIN it.variant va " +
            "WHERE va.product.productID = :productId")
    long countSoldQuantityByProductId(@Param("productId") String productId);


    //tìm sp theo tên
    @Query("SELECT p FROM Product p WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchProductsByName(@Param("keyword") String keyword);


}
