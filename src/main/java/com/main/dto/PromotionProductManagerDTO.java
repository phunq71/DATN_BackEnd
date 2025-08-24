package com.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromotionProductManagerDTO {
    private Integer promotionProductId;
    private String promotionId;
    private String productId;
    private Integer quantityUsed;
    private Integer quantityRemaining;
    private Byte discountPercent;
    private String note;
}
