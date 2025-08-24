package com.main.service;

import com.main.dto.PromotionProductManagerDTO;
import com.main.entity.Promotion;
import com.main.entity.PromotionProduct;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PromotionProductService {
    public PromotionProduct getByPromotion(Promotion promotion);

    List<PromotionProductManagerDTO> findPromotionByPromotionId(@Param("promotionID") String promotionID);
    public List<String> findProductOfNewPromotion(LocalDateTime startDate, LocalDateTime endDate, String promotionId);
    void savePromotionProduct(PromotionProduct promotionProduct);
    public PromotionProduct findPromotionProductByPromotionId(Integer promotionID);
    Boolean deletePromotionProduct(Integer promotionProduct);
}
