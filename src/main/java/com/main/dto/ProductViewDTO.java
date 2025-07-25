package com.main.dto;

import com.main.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductViewDTO {
    private String productID;
    private String productName;
    private String parentCategoryId;
    private String childCategoryId;
    private String ImageUrl;
    private BigDecimal price;
    private String variantID;
    private Byte discountPercent;
    private Boolean isFavorite;
    private Boolean isHot;
    private Boolean isNew;
    private Double avg_rating;
    private List<ColorOption> options;
    private Integer totalLikes;

    public ProductViewDTO(String productID,Byte discountPercent) {
        this.productID = productID;
        this.discountPercent = discountPercent;
    }
}
