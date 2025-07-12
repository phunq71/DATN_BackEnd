package com.main.repository;

import com.main.entity.Product;
import com.main.entity.Variant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VariantRepository extends JpaRepository<Variant, String> {
    //Lấy list sp bang product
    List<Variant> findByProduct(Product product);

        @Query(value = """
        SELECT v.*
        FROM Variants v
        JOIN Products p ON v.ProductID = p.ProductID
        WHERE v.CreatedDate >= DATEADD(DAY, -20, GETDATE())
          AND p.CreatedDate < DATEADD(DAY, -20, GETDATE())
    """, nativeQuery = true)
        List<Variant> findNewVariantsOfOldProducts();

    @Query(value = """
    SELECT CASE 
        WHEN COUNT(*) > 0 THEN 1 
        ELSE 0 
    END
    FROM Variants v
    JOIN Products p ON v.ProductID = p.ProductID
    WHERE v.CreatedDate >= CAST(DATEADD(DAY, -20, GETDATE()) AS DATE)
      AND v.VariantID = :variantID
""", nativeQuery = true)
    int isNewVariantOf(@Param("variantID") String variantID);
}
