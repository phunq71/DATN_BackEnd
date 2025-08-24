package com.main.service;

import com.main.dto.PromotionVoucherManagerDTO;
import com.main.entity.Promotion;

import java.util.List;

public interface PromotionService {
    public List<PromotionVoucherManagerDTO> getPromotions();
    public Promotion save(Promotion promotion);
    public Promotion getByPromotionId(String promotionId);
    String findTop1ByOrderByPromotionIDDesc();
    Boolean deletePromotion(String promotionId);
}
