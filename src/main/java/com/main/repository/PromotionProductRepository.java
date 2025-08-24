package com.main.repository;

import com.main.dto.PromotionProductManagerDTO;
import com.main.entity.Promotion;
import com.main.entity.PromotionProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PromotionProductRepository  extends JpaRepository<PromotionProduct, Integer> {
    PromotionProduct findByPromotion(Promotion promotion);

    @Query("""
    SELECT  pp.promotionProductID, pp.promotion.promotionID, pp.product.productID
            , pp.quantityUsed, pp.quantityRemaining
            ,pp.discountPercent , pp.note
            FROM PromotionProduct pp
            WHERE pp.promotion.promotionID = :promotionID
    """)
    List<PromotionProductManagerDTO> findPromotionByPromotionId(@Param("promotionID") String promotionID);
    PromotionProduct promotion(Promotion promotion);

    PromotionProduct findPromotionProductByPromotionProductID(Integer promotionProductID);


}
