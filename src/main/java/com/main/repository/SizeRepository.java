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
SELECT 
    v.variantID,
    s.sizeID,
    s.code,
    i.itemId,
    COALESCE(SUM(inv.quantity) , 0)
FROM Variant v
JOIN Item i ON i.variant.variantID = v.variantID
JOIN Size s ON s.sizeID = i.size.sizeID
LEFT JOIN Inventory inv ON inv.item.itemId = i.itemId
    AND inv.facility.isUse = true
WHERE v.variantID = :variantId
GROUP BY v.variantID, s.sizeID, s.code, i.itemId
""")
    List<SizeDTO> getSizeDTOByVariantID(@Param("variantId") String variantId);



}
