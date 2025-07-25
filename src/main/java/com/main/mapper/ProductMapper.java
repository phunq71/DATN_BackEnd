package com.main.mapper;

import com.main.dto.Product_DetailDTO;
import com.main.entity.Product;
import com.main.dto.ProductByCategoryDTO;
import com.main.entity.Image;
import com.main.entity.Variant;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {
    //mapper gộp cả variant đc map
    public static Product_DetailDTO toDTOO(Product product) {
        if (product == null) return null;
        Product_DetailDTO dto = new Product_DetailDTO();
        dto.setProductId(product.getProductID());
        dto.setProductName(product.getProductName());
        dto.setVariants(VariantMapper.toDTOList(product.getVariants()));
        return dto;
    }
    //chuyển đối tượng Product->ProductByCategoryDTO
    public static ProductByCategoryDTO toDTO(Product product) {
        //lọc ds các biến thể đang hoạt động
        List<Variant> variants = product.getVariants().stream()
                .filter(Variant::getIsUse)
                .collect(Collectors.toList());//chuyển thành ds mới

        // Lấy ảnh chính
        String mainImageUrl = product.getVariants().stream()
                .filter(v -> v.getIsUse() && v.getIsMainVariant()) // lọc biến thể chính
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

        String variantMainId = variants.stream()
                .filter(Variant::getIsMainVariant)
                .map(Variant::getVariantID)
                .findFirst()
                .orElse(
                        variants.isEmpty() ? null : variants.get(0).getVariantID()
                );

        //trả về dto
        return new ProductByCategoryDTO(
                product.getProductID(),
                product.getProductName(),
                mainImageUrl,
                price,
                colors,
                variantMainId
        );
    }

}
