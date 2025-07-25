package com.main.mapper;

import com.main.dto.ProductFavoriteDTO;
import com.main.entity.Image;
import com.main.entity.Product;
import com.main.entity.Variant;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FavoriteMapper {
    public ProductFavoriteDTO toDTO(Product product) {
        // Lọc các biến thể đang hoạt động
        List<Variant> variants = product.getVariants().stream()
                .filter(Variant::getIsUse)
                .collect(Collectors.toList());

        // Tìm biến thể chính
        Variant mainVariant = variants.stream()
                .filter(Variant::getIsMainVariant)
                .findFirst()
                .orElse(
                        variants.isEmpty() ? null : variants.get(0)
                );

        // Lấy giá và màu từ biến thể chính
        BigDecimal price = mainVariant != null ? mainVariant.getPrice() : BigDecimal.ZERO;
        String color = mainVariant != null ? mainVariant.getColor() : null;

        // Lấy ảnh chính từ biến thể chính
        String mainImageUrl = null;
        if (mainVariant != null && mainVariant.getImages() != null) {
            mainImageUrl = mainVariant.getImages().stream()
                    .filter(Image::getIsMainImage)
                    .map(Image::getImageUrl)
                    .findFirst()
                    .orElse(null);
        }

        // Trả về DTO
        return new ProductFavoriteDTO(
                product.getProductID(),
                product.getProductName(),
                mainImageUrl,
                price,
                color,
                product.getTargetCustomer(),
                true,
                product.getCategory().getParent().getCategoryId(),
                product.getCategory().getCategoryId()// luôn là yêu thích
        );
    }

}
