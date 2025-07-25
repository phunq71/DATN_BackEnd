package com.main.mapper;

import com.main.dto.OrderPreviewDTO;
import com.main.entity.Cart;
import com.main.entity.Image;
import com.main.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class CartMapper {
    private final ProductRepository productRepository;

    public OrderPreviewDTO toOrderPreviewDTO(Cart cart) {
        String image_url = cart.getItem().getVariant().getImages().stream()
                .filter(Image::getIsMainImage)
                .map(Image::getImageUrl)
                .findFirst()
                .orElse("");

        Byte discountPercent = productRepository.findDiscountPercentByProductID(
                cart.getItem().getVariant().getProduct().getProductID()
        );
        if (discountPercent == null) {
            discountPercent = 0;
        }

        BigDecimal price = cart.getItem().getVariant().getPrice(); // giá gốc
        Integer quantity = cart.getQuantity();

        // ✅ Tính giá sau khi giảm cho 1 sp
        BigDecimal discountedPrice = price.subtract(
                price.multiply(BigDecimal.valueOf(discountPercent).divide(BigDecimal.valueOf(100)))
        );

        // ✅ Tổng tiền sau giảm
        BigDecimal total = discountedPrice.multiply(BigDecimal.valueOf(quantity));

        // id KM
        Integer idPromotion = productRepository.findPromotionProductIdByProductID(cart.getItem().getVariant().getProduct().getProductID());

        // ✅ Khởi tạo DTO
        OrderPreviewDTO orderPreviewDTO = new OrderPreviewDTO(
                cart.getItem().getItemId(),
                cart.getItem().getVariant().getProduct().getProductName(),
                image_url,
                cart.getItem().getSize().getCode(),
                cart.getItem().getVariant().getColor(),
                price,               // giữ lại giá gốc
                discountPercent,
                quantity,
                total,
                discountedPrice,
                idPromotion
        );

        return orderPreviewDTO;
    }


}
