package com.main.repository;

import com.main.dto.SizeDTO;
import com.main.entity.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SizeRepository extends JpaRepository<Size, Integer> {

    @Query("""
        SELECT v.variantID
                ,s.sizeID
                ,s.code
                ,i.itemId
                ,COALESCE(inv.quantity, 0) FROM Variant v
        JOIN Item i on i.variant.variantID = v.variantID
        JOIN Size s on s.sizeID= i.size.sizeID
        LEFT JOIN Inventory inv ON inv.item.itemId = i.itemId
        WHERE v.variantID = :variantId
        """)
    List<SizeDTO> getSizeDTOByVariantID(@Param("variantId") String variantId);
}
