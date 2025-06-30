package com.main.repository;

import com.main.dto.ItemCartDTO;
import com.main.entity.Cart;
import com.main.entity.CartId;
import com.main.entity.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, CartId> {

    @Query("SELECT " +
            "i.itemId, " +
            "p.productName, " +
            "v.variantID, " +
            "v.color, " +
            "s.sizeID, " +
            "s.code, " +
            "v.price " +
            ",(SELECT img.imageUrl FROM Image img WHERE img.variant = v AND img.isMainImage = true) " +
            "FROM Item i " +
            "JOIN Variant v on v.variantID = i.variant.variantID " +
            "JOIN Product p on p.productID = v.product.productID " +
            "JOIN Size s on s.sizeID = i.size.sizeID "
            + "WHERE i.itemId =:itemId")
    public ItemCartDTO getItemCart(@Param("itemId") int itemId);

    @Query("SELECT v.color FROM Variant v " +
            "WHERE v.product = (SELECT v2.product FROM Variant v2 WHERE v2.variantID = :variantID)")
    List<String> getColorByVariantID(@Param("variantID") String variantID);

    @Query("SELECT i.size FROM Item i where i.variant.variantID=:variantID")
    public List<Size> getSizeByVariantID(@Param("variantID") String variantID);
}
