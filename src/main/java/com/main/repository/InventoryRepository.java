package com.main.repository;

import com.main.entity.Inventory;
import com.main.entity.InventoryId;
import org.springframework.data.jpa.repository.JpaRepository;
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


}
