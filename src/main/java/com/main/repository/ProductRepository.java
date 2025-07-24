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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository  extends JpaRepository<Product, String> {
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
    SELECT COALESCE(pp.discountPercent, 0)
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

    @Query("""
        SELECT DISTINCT p FROM Product p
        JOIN Variant mainVar ON mainVar.product = p AND mainVar.isMainVariant = true
        LEFT JOIN Item i ON i.variant = mainVar
        LEFT JOIN OrderDetail od ON od.item = i
        WHERE (:color IS NULL OR EXISTS (
            SELECT 1 FROM Variant v2 WHERE v2.product = p AND v2.color = :color AND v2.isUse = true
        ))
        AND (:brand IS NULL OR p.brand = :brand)
        AND (:targetCustomer IS NULL OR p.targetCustomer = :targetCustomer)
        AND (:priceFrom IS NULL OR mainVar.price >= :priceFrom)
        AND (:priceTo IS NULL OR mainVar.price <= :priceTo)
        AND (:minRating IS NULL OR (
            SELECT COALESCE(AVG(r2.rating), 0)
            FROM Variant v2
            JOIN Item i2 ON i2.variant = v2
            JOIN OrderDetail od2 ON od2.item = i2
            JOIN Review r2 ON r2.orderDetail = od2
            WHERE v2.product = p
        ) >= :minRating)
        AND (
              ((:categoryId IS NOT NULL AND :categoryId <> '') AND p.category.categoryId = :categoryId)
                OR ((:categoryId IS NULL OR :categoryId = '') AND :parentCategoryId IS NOT NULL AND p.category.parent.categoryId = :parentCategoryId)
        )
        """)
    Page<Product> filterProductsWithReviewOnly(
            @Param("color") String color,
            @Param("brand") String brand,
            @Param("priceFrom") BigDecimal priceFrom,
            @Param("priceTo") BigDecimal priceTo,
            @Param("minRating") Double minRating,
            @Param("targetCustomer") String targetCustomer,
            @Param("categoryId") String categoryId,
            @Param("parentCategoryId") String parentCategoryId,
            Pageable pageable);

    //tìm sp theo tên
    @Query("SELECT p FROM Product p WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchProductsByName(@Param("keyword") String keyword);


    //mặc định
    @Query("""
    SELECT DISTINCT p FROM Product p
    JOIN Variant mainVar ON mainVar.product = p AND mainVar.isMainVariant = true
    LEFT JOIN Item i ON i.variant = mainVar
    LEFT JOIN OrderDetail od ON od.item = i
    WHERE 
        (:keyword IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%')))
        AND (:color IS NULL OR EXISTS (
            SELECT 1 FROM Variant v2 
            WHERE v2.product = p AND v2.color = :color AND v2.isUse = true
        ))
        AND (:brand IS NULL OR p.brand = :brand)
        AND (:targetCustomer IS NULL OR p.targetCustomer = :targetCustomer)
        AND (:priceFrom IS NULL OR mainVar.price >= :priceFrom)
        AND (:priceTo IS NULL OR mainVar.price <= :priceTo)
        AND (:minRating IS NULL OR (
            SELECT COALESCE(AVG(r2.rating), 0)
            FROM Variant v2
            JOIN Item i2 ON i2.variant = v2
            JOIN OrderDetail od2 ON od2.item = i2
            JOIN Review r2 ON r2.orderDetail = od2
            WHERE v2.product = p
        ) >= :minRating)
        AND (
            ((:categoryId IS NOT NULL AND :categoryId <> '') AND p.category.categoryId = :categoryId)
            OR ((:categoryId IS NULL OR :categoryId = '') AND :parentCategoryId IS NOT NULL AND p.category.parent.categoryId = :parentCategoryId)
        )
""")
    Page<Product> searchAndFilterProductsDefault(
            @Param("keyword") String keyword,
            @Param("color") String color,
            @Param("brand") String brand,
            @Param("priceFrom") BigDecimal priceFrom,
            @Param("priceTo") BigDecimal priceTo,
            @Param("minRating") Double minRating,
            @Param("targetCustomer") String targetCustomer,
            @Param("categoryId") String categoryId,
            @Param("parentCategoryId") String parentCategoryId,
            Pageable pageable
    );

    //giá tăng dần
    @Query("""
SELECT DISTINCT p, mainVar.price FROM Product p
JOIN Variant mainVar ON mainVar.product = p AND mainVar.isMainVariant = true
LEFT JOIN Item i ON i.variant = mainVar
LEFT JOIN OrderDetail od ON od.item = i
WHERE 
    (:keyword IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%')))
    AND (:color IS NULL OR EXISTS (
        SELECT 1 FROM Variant v2 
        WHERE v2.product = p AND v2.color = :color AND v2.isUse = true
    ))
    AND (:brand IS NULL OR p.brand = :brand)
    AND (:targetCustomer IS NULL OR p.targetCustomer = :targetCustomer)
    AND (:priceFrom IS NULL OR mainVar.price >= :priceFrom)
    AND (:priceTo IS NULL OR mainVar.price <= :priceTo)
    AND (:minRating IS NULL OR (
        SELECT COALESCE(AVG(r2.rating), 0)
        FROM Variant v2
        JOIN Item i2 ON i2.variant = v2
        JOIN OrderDetail od2 ON od2.item = i2
        JOIN Review r2 ON r2.orderDetail = od2
        WHERE v2.product = p
    ) >= :minRating)
    AND (
        ((:categoryId IS NOT NULL AND :categoryId <> '') AND p.category.categoryId = :categoryId)
        OR ((:categoryId IS NULL OR :categoryId = '') AND :parentCategoryId IS NOT NULL AND p.category.parent.categoryId = :parentCategoryId)
    )
ORDER BY mainVar.price ASC
""")
    Page<Object[]> searchAndFilterProductsPriceAsc(
            @Param("keyword") String keyword,
            @Param("color") String color,
            @Param("brand") String brand,
            @Param("priceFrom") BigDecimal priceFrom,
            @Param("priceTo") BigDecimal priceTo,
            @Param("minRating") Double minRating,
            @Param("targetCustomer") String targetCustomer,
            @Param("categoryId") String categoryId,
            @Param("parentCategoryId") String parentCategoryId,
            Pageable pageable
    );


    //giá giảm dần
    @Query("""
SELECT DISTINCT p, mainVar.price FROM Product p
JOIN Variant mainVar ON mainVar.product = p AND mainVar.isMainVariant = true
LEFT JOIN Item i ON i.variant = mainVar
LEFT JOIN OrderDetail od ON od.item = i
WHERE 
    (:keyword IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%')))
    AND (:color IS NULL OR EXISTS (
        SELECT 1 FROM Variant v2 
        WHERE v2.product = p AND v2.color = :color AND v2.isUse = true
    ))
    AND (:brand IS NULL OR p.brand = :brand)
    AND (:targetCustomer IS NULL OR p.targetCustomer = :targetCustomer)
    AND (:priceFrom IS NULL OR mainVar.price >= :priceFrom)
    AND (:priceTo IS NULL OR mainVar.price <= :priceTo)
    AND (:minRating IS NULL OR (
        SELECT COALESCE(AVG(r2.rating), 0)
        FROM Variant v2
        JOIN Item i2 ON i2.variant = v2
        JOIN OrderDetail od2 ON od2.item = i2
        JOIN Review r2 ON r2.orderDetail = od2
        WHERE v2.product = p
    ) >= :minRating)
    AND (
        ((:categoryId IS NOT NULL AND :categoryId <> '') AND p.category.categoryId = :categoryId)
        OR ((:categoryId IS NULL OR :categoryId = '') AND :parentCategoryId IS NOT NULL AND p.category.parent.categoryId = :parentCategoryId)
    )
ORDER BY mainVar.price DESC
""")
    Page<Object[]> searchAndFilterProductsPriceDesc(
            @Param("keyword") String keyword,
            @Param("color") String color,
            @Param("brand") String brand,
            @Param("priceFrom") BigDecimal priceFrom,
            @Param("priceTo") BigDecimal priceTo,
            @Param("minRating") Double minRating,
            @Param("targetCustomer") String targetCustomer,
            @Param("categoryId") String categoryId,
            @Param("parentCategoryId") String parentCategoryId,
            Pageable pageable
    );



}

