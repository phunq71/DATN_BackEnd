package com.main.repository;

import com.main.dto.CartDTO;
import com.main.dto.ItemCartDTO;
import com.main.dto.MiniCartDTO;
import com.main.entity.Cart;
import com.main.entity.CartId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, CartId> {

    @Query("""
            SELECT i.itemId,
                   p.productName,
                   v.variantID,
                   v.color,
                   s.sizeID, 
                   s.code, 
                   v.price, 
                   (SELECT img.imageUrl 
                    FROM Image img 
                    WHERE img.variant = v AND img.isMainImage = true), 
                   CASE WHEN (SELECT COUNT(inv) 
                              FROM Inventory inv 
                              WHERE inv.item = i AND inv.facility.isUse = true) > 0 THEN true ELSE false END, 
                   (SELECT SUM(inv.quantity) 
                    FROM Inventory inv 
                    WHERE inv.item = i AND inv.facility.isUse = true)
            FROM Item i 
            JOIN Variant v ON v.variantID = i.variant.variantID 
            JOIN Product p ON p.productID = v.product.productID 
            JOIN Size s ON s.sizeID = i.size.sizeID 
            WHERE p.productID = (
                SELECT v2.product.productID 
                FROM Item i2 
                JOIN Variant v2 ON v2.variantID = i2.variant.variantID 
                WHERE i2.itemId = :itemId
            )
            """)
    public List<ItemCartDTO> getItemsBySameProduct(@Param("itemId") int itemId);


    @Query("SELECT p.productName, v.price, img.imageUrl " +
            "FROM Product p " +
            "JOIN Variant v on v.product.productID=p.productID " +
            "JOIN Image img on v.variantID=img.variant.variantID " +
            "JOIN Item i on i.variant.variantID=v.variantID " +
            "WHERE i.itemId=:itemID AND img.isMainImage=true")
    public MiniCartDTO getMiniCarts(@Param("itemID") int itemID);

    @Query("""
            SELECT c.id.customer, c.id.item, c.quantity, c.latestDate FROM Cart c
            WHERE c.id.customer = :customerId ORDER BY c.latestDate DESC
            """)
    public List<CartDTO> getCartsByCustomerId(@Param("customerId") String customerId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Cart c WHERE c.id.customer=:customerId")
    public void clearCartsByCustomerId(@Param("customerId") String customerId);

    Cart getCartByItem_itemIdAndCustomer_customerId(Integer itemItemId, String customerId);

}
