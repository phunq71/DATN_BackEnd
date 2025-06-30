package com.main.repository;

import com.main.dto.ItemCartDTO;
import com.main.entity.Cart;
import com.main.entity.CartId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, CartId> {

    @Query("SELECT c.id, " +
            "i.itemId, " +
            "p.productName, " +
            "v.variantID, " +
            "v.color, " +
            "s.sizeID, " +
            "s.code, " +
            "v.price, " +
            "(SELECT img.imageUrl FROM Image img WHERE img.variant = v AND img.isMainImage = true)" +
            "FROM Cart c " +
            "JOIN c.item i " +
            "JOIN i.variant v " +
            "JOIN v.product p " +
            "JOIN i.size s "
            + "WHERE i.itemId =:itemId")
    public ItemCartDTO getItemCart();
}
