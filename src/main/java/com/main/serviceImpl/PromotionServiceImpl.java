package com.main.serviceImpl;

import com.main.dto.PromotionVoucherManagerDTO;
import com.main.entity.Promotion;
import com.main.entity.PromotionProduct;
import com.main.entity.Voucher;
import com.main.repository.PromotionProductRepository;
import com.main.repository.PromotionRepository;
import com.main.repository.VoucherRepository;
import com.main.service.PromotionService;
import com.main.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {
    private final PromotionRepository promotionRepository;
    private final PromotionProductRepository promotionProductRepository;
    private final VoucherService voucherService;
    private final VoucherRepository voucherRepository;

    @Override
    public List<PromotionVoucherManagerDTO> getPromotions() {
        return promotionRepository.getPromotions();
    }

    @Override
    public Promotion save(Promotion promotion) {
        return promotionRepository.save(promotion);
    }

    @Override
    public Promotion getByPromotionId(String promotionId) {
        return promotionRepository.findById(promotionId).get();
    }

    @Override
    public String findTop1ByOrderByPromotionIDDesc() {
        Promotion promotion = promotionRepository.findTop1ByOrderByPromotionIDDesc();
        return promotion != null ? promotion.getPromotionID() : null;
    }

    @Transactional
    @Override
    public Boolean deletePromotion(String promotionId) {
        try {
            Optional<Promotion> optionalPromotion = promotionRepository.findById(promotionId);
            if (optionalPromotion.isEmpty()) {
                return false;
            }
            Promotion promotion = optionalPromotion.get();
            if ("ProductDiscount".equals(promotion.getType())) {
                promotionProductRepository.deleteAll(promotion.getPromotionProducts());
            } else {
                voucherRepository.deleteAll(promotion.getVouchers());
            }
            promotionRepository.delete(promotion);
            return true;
        } catch (Exception e) {
            return false; // thất bại
        }
    }
}
