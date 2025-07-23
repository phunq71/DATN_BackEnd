package com.main.repository;

import com.main.dto.ReturnItemDTO;
import com.main.entity.ReturnItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReturnItemRepository extends JpaRepository<ReturnItem, Integer> {
    @Query("""
            SELECT
               od.orderDetailID,
               img.imageUrl,
               p.productName,
               s.code,
               v.color,
               od.unitPrice,
               od.promotionProduct.discountPercent,
               od.quantity
           FROM OrderDetail od
           JOIN od.item i
           JOIN i.variant v
           JOIN v.product p
           JOIN i.size s
           JOIN od.order o
           JOIN Image img ON img.variant = v AND img.isMainImage = true
           WHERE o.orderID = :orderID
           """)
    List<ReturnItemDTO> getReturnItemByOrderID(@Param("orderID") int orderID);
}
