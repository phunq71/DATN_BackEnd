package com.main.repository;


import com.main.entity.Facility;

import com.main.dto.InvDTO_CheckLimitQT;
import com.main.dto.InventoryDTO;

import com.main.entity.Inventory;
import com.main.entity.InventoryId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, InventoryId> {
    @Query("""
                SELECT SUM(inv.quantity) 
                FROM Inventory inv 
                JOIN inv.item it 
                JOIN it.variant v 
                JOIN it.size s 
                WHERE v.variantID = :variantId 
                  AND s.code = :code 
                  AND inv.facility.type = 'K'
                 AND inv.facility.isUse = true
            """)
    Integer getQuantityByVariantAndSizeCode(@Param("variantId") String variantId,
                                            @Param("code") String code);


    @Query("""
                SELECT SUM(inv.quantity)
                FROM Inventory inv
                WHERE inv.item.itemId = :itemId
                  AND inv.facility.isUse = true
            """)
    public Integer getStockQuantityByItemId(@Param("itemId") int itemId);

    public Inventory getInventoryById(InventoryId inventoryId);


    boolean existsByFacilityAndQuantityGreaterThan(Facility facility, int quantity);

    @Query("""
    SELECT
        i.item.itemId,
        f.facilityId,
        CONCAT(p.productName, ' - ', v.color, ' - ', sz.code),
        i.minQt,
        i.maxQt,
        i.quantity
    FROM Inventory i
    JOIN i.facility f
    JOIN i.item it
    JOIN it.variant v
    JOIN v.product p
    JOIN it.size sz
    WHERE f.facilityId = :facilityId
      AND f.type = 'W'
""")
    Page<InventoryDTO> getInventoriesWareHouse(Pageable pageable, @Param("facilityId") String facilityId);

    @Query(
            value = """
        WITH pair_ AS (
            SELECT s.facilityId AS shopId, k.facilityId AS whId
            FROM Facilities s
            JOIN Facilities k ON k.parentId = s.facilityId AND k.type = 'K'
            WHERE s.FacilityID = :facilityId AND s.type = 'S'
        ),
        si AS (
            SELECT i.itemId, i.minQt, i.maxQt, i.quantity
            FROM Inventories i
            WHERE i.facilityId = (SELECT shopId FROM pair_)
        ),
        ki AS (
            SELECT i.itemId, i.minQt, i.maxQt, i.quantity
            FROM Inventories i
            WHERE i.facilityId = (SELECT whId FROM pair_)
        ),
        combined AS (
            SELECT
                COALESCE(si.itemId, ki.itemId) AS itemId,
                si.minQt AS shopMinQt,
                si.maxQt AS shopMaxQt,
                si.quantity AS shopQuantity,
                ki.minQt AS stockMinQt,
                ki.maxQt AS stockMaxQt,
                ki.quantity AS stockQuantity
            FROM si
            FULL OUTER JOIN ki ON ki.itemId = si.itemId
        )
        SELECT
            c.itemId,
            p.shopId AS facilityId,
            CONCAT(pr.productName, ' - ', v.color, ' - ', sz.code) AS sku,
            ISNULL(c.shopMinQt, 0) AS minShopQT,
            ISNULL(c.shopMaxQt, 0) AS maxShopQT,
            ISNULL(c.stockMinQt, 0) AS minStockQT,
            ISNULL(c.stockMaxQt, 0) AS maxStockQT,
            ISNULL(c.stockQuantity, 0) AS stockQuantity,
            ISNULL(c.shopQuantity, 0) AS shopQuantity,
            p.whId AS stockId
        FROM combined c
        CROSS JOIN pair_ p
        JOIN Items it ON it.itemId = c.itemId  
        JOIN Variants v ON v.variantId = it.variantId
        JOIN Products pr ON pr.productId = v.productId
        JOIN Sizes sz ON sz.sizeId = it.sizeId
        ORDER BY
            CASE
                WHEN ISNULL(c.shopMaxQt, 0) = 0 AND ISNULL(c.stockMaxQt, 0) = 0 THEN 0
                WHEN ISNULL(c.shopMaxQt, 0) = 0 THEN ISNULL(c.stockQuantity, 0) * 1.0 / NULLIF(ISNULL(c.stockMaxQt, 1), 0)
                WHEN ISNULL(c.stockMaxQt, 0) = 0 THEN ISNULL(c.shopQuantity, 0) * 1.0 / NULLIF(ISNULL(c.shopMaxQt, 1), 0)
                ELSE CASE
                    WHEN (ISNULL(c.shopQuantity, 0) * 1.0 / NULLIF(ISNULL(c.shopMaxQt, 1), 0)) <
                         (ISNULL(c.stockQuantity, 0) * 1.0 / NULLIF(ISNULL(c.stockMaxQt, 1), 0))
                    THEN (ISNULL(c.shopQuantity, 0) * 1.0 / NULLIF(ISNULL(c.shopMaxQt, 1), 0))
                    ELSE (ISNULL(c.stockQuantity, 0) * 1.0 / NULLIF(ISNULL(c.stockMaxQt, 1), 0))
                END
            END ASC
        """,
            countQuery = """
        WITH pair_ AS (
            SELECT s.facilityId AS shopId, k.facilityId AS whId
            FROM Facilities s
            JOIN Facilities k ON k.parentId = s.facilityId AND k.type = 'K'
            WHERE s.FacilityID = :facilityId AND s.type = 'S'
        ),
        si AS (
            SELECT i.itemId FROM Inventories i
            WHERE i.facilityId = (SELECT shopId FROM pair_)
        ),
        ki AS (
            SELECT i.itemId FROM Inventories i
            WHERE i.facilityId = (SELECT whId FROM pair_)
        ),
        combined AS (
            SELECT COALESCE(si.itemId, ki.itemId) AS itemId
            FROM si
            FULL OUTER JOIN ki ON ki.itemId = si.itemId
        )
        SELECT COUNT(*) FROM combined
        """,
            nativeQuery = true
    )
    Page<InventoryDTO> getInventoryDTO(Pageable pageable, @Param("facilityId") String facilityId);



    @Query("""
        SELECT it.itemId
            , inv.quantity
            , inv.maxQt
        FROM Inventory inv
        JOIN inv.item it
        JOIN inv.facility f
        WHERE it.itemId in :itemIds
        AND f.facilityId = :facilityId
        """)
    List<InvDTO_CheckLimitQT> getInventoryByItemIdsAndFacilityId(@Param("itemIds") List<Integer> itemIds, @Param("facilityId") String facilityId);


    @Modifying
    @Query("""
        UPDATE Inventory i 
        SET i.quantity = i.quantity + :delta
        WHERE i.id = :id
        """)
    void updateQuantity(@Param("id") InventoryId id, @Param("delta") int delta);


}
