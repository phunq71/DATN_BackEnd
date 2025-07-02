package com.main.mapper;

import com.main.dto.ProductByCategory;
import com.main.entity.Image;
import com.main.entity.Product;
import com.main.entity.Variant;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {
    //chuyển đối tượng Product->ProductByCategoryDTO
    public static ProductByCategory toDTO(Product product) {
        //lọc ds các biến thể đang hoạt động
        List<Variant> variants = product.getVariants().stream()
                .filter(Variant::getIsUse)
                .collect(Collectors.toList());//chuyển thành ds mới

        // Lấy ảnh chính
        String mainImageUrl = variants.stream()
                .flatMap(v -> v.getImages().stream())
                .filter(Image::getIsMainImage)
                .map(Image::getImageUrl)
                .findFirst()
                .orElse(null);

        // Lấy màu sắc từ các variant
        List<String> colors = variants.stream()
                .map(Variant::getColor)
                .distinct() //lọc trùng
                .collect(Collectors.toList());

        // Lấy giá từ variant chính, nếu không có thì lấy variant đầu tiên
        BigDecimal price = variants.stream()
                .filter(Variant::getIsMainVariant)
                .map(Variant::getPrice)
                .findFirst()
                .orElse(
                        variants.isEmpty() ? BigDecimal.ZERO : variants.get(0).getPrice()
                );

        //trả về dto
        return new ProductByCategory(
                product.getProductID(),
                product.getProductName(),
                mainImageUrl,
                price,
                colors
        );
    }
}
