package com.main.serviceImpl;

import com.main.dto.PromotionProductManagerDTO;
import com.main.entity.Product;
import com.main.entity.Promotion;
import com.main.entity.PromotionProduct;
import com.main.entity.Voucher;
import com.main.repository.OrderDetailRepository;
import com.main.repository.ProductRepository;
import com.main.repository.PromotionProductRepository;
import com.main.repository.PromotionRepository;
import com.main.service.PromotionProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PromotionProductServiceImpl implements PromotionProductService {
    private final PromotionProductRepository promotionProductRepository;
    private final PromotionRepository promotionRepository;
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;

    @Override
    public PromotionProduct getByPromotion(Promotion promotion) {
        return promotionProductRepository.findByPromotion(promotion);
    }

    @Override
    public List<PromotionProductManagerDTO> findPromotionByPromotionId(String promotionID) {
        return promotionProductRepository.findPromotionByPromotionId(promotionID);
    }

    @Override
    public List<String> findProductOfNewPromotion(LocalDateTime startDate, LocalDateTime endDate, String promotionId) {
        List<Promotion> list = promotionRepository.findPromotionByStartDateAndEndDate(startDate, endDate);
        List<String> list1 = new ArrayList<>();
        for (Promotion promotion : list) {
            promotion.getPromotionProducts().forEach(promotionProduct -> {
                list1.add(promotionProduct.getProduct().getProductID());
            });
        }
        List<Product> products = productRepository.findAll();
        List<String> list2 = new ArrayList<>();
        for (Product product : products) {
            if(list1.contains(product.getProductID())){
                products.remove(product.getProductID());
            }else {
                list2.add(product.getProductID());
            }
        }

        Promotion promotion = promotionRepository.findById(promotionId).get();
        promotion.getPromotionProducts().forEach(promotionProduct -> {
            list2.add(promotionProduct.getProduct().getProductID());
        });
        return list2;
    }

    @Override
    public void savePromotionProduct(PromotionProduct promotionProduct) {
        promotionProductRepository.save(promotionProduct);
    }

    @Override
    public PromotionProduct findPromotionProductByPromotionId(Integer promotionID) {
        return promotionProductRepository.findPromotionProductByPromotionProductID(promotionID);
    }


    @Override
    public Boolean deletePromotionProduct(Integer promotionProductID) {
        // Kiểm tra voucher có tồn tại không
        Optional<PromotionProduct> optionalVoucher = promotionProductRepository.findById(promotionProductID);
        if (optionalVoucher.isEmpty()) {
            return false;
        }
        PromotionProduct promotionProduct = optionalVoucher.get();
        boolean existsInOrders = orderDetailRepository.existsByPromotionProduct(promotionProduct);
        if (existsInOrders) {
            return false; // đang được tham chiếu, không cho xóa
        }
        promotionProductRepository.delete(promotionProduct);
        return true;
    }
}
