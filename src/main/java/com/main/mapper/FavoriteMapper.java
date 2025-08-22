package com.main.mapper;

import com.main.dto.ColorOption;
import com.main.dto.ProductFavoriteDTO;
import com.main.entity.Image;
import com.main.entity.Product;
import com.main.entity.Variant;
import com.main.repository.FavoriteRepository;
import com.main.repository.ProductRepository;
import com.main.repository.ReviewRepository;
import com.main.utils.DataUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FavoriteMapper {
    private final ProductRepository productRepository;
    private final FavoriteRepository favoriteRepository;
    private final ReviewRepository reviewRepository;



    public ProductFavoriteDTO toDTO(Product product, List<String> hotProductIDs) {
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
        ProductFavoriteDTO pro = new ProductFavoriteDTO(
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


        pro.setDiscountPercent(productRepository.findDiscountPercentByProductID(product.getProductID()));
        pro.setIsHot(hotProductIDs.contains(product.getProductID()));
        pro.setIsNew(productRepository.isNewProduct(product.getProductID())>0);
        pro.setTotalLikes(favoriteRepository.countFavoriteByProduct_ProductID(product.getProductID()));
        pro.setAvg_rating(calculateAvgRating(product.getProductID()));
        pro.setOptions(extractColorOptions(product.getVariants()));

        return pro;
    }

    private Double calculateAvgRating(String productID) {
        Double avg = reviewRepository.findAverageRatingByProductId(productID);
        if (avg != null) {
            BigDecimal rounded = new BigDecimal(avg).setScale(1, RoundingMode.HALF_UP);
            return rounded.doubleValue();
        }
        return null;
    }

    private List<ColorOption> extractColorOptions(List<Variant> variants) {
        List<ColorOption> colorList = DataUtils.getListColors();

        return variants.stream()
                .filter(Variant::getIsUse) // Lọc các variant đang hoạt động
                .map(variant -> {
                    String colorName = variant.getColor();
                    return colorList.stream()
                            .filter(color -> color.getTen().equalsIgnoreCase(colorName))
                            .findFirst()
                            .orElse(null);
                })
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

}
