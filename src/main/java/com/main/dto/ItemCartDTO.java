package com.main.dto;

import com.main.entity.CartId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemCartDTO {
    private Integer itemID;
    private String productName;
    private String variantID;
    private String Color;
    private Integer sizeID;
    private String sizeCode;
    private BigDecimal price;
    private String mainImage;

    private int quantity;
    private Boolean isInStock;
    private Long stockQuantity=0L;
    private boolean isChosen=false; //cart được chọn

    public ItemCartDTO(Integer itemID, String productName, String variantID, String color, Integer sizeID, String sizeCode, BigDecimal price, String mainImage, Boolean isInStock, Long stockQuantity) {
        this.itemID = itemID;
        this.productName = productName;
        this.variantID = variantID;
        Color = color;
        this.sizeID = sizeID;
        this.sizeCode = sizeCode;
        this.price = price;
        this.mainImage = mainImage;
        this.isInStock = isInStock;
        this.stockQuantity = stockQuantity;
    }
}
