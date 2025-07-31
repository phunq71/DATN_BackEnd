package com.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SizeDTO {
    private String variantId;
    private Integer sizeId;
    private String sizeCode;
    private Integer itemId;
    private Long stockQuantity;

    private Boolean isInStock;

    public SizeDTO(String variantId, Integer sizeId, String sizeCode, Integer itemId, Long stockQuantity) {
        this.variantId = variantId;
        this.sizeId = sizeId;
        this.sizeCode = sizeCode;
        this.itemId = itemId;
        this.stockQuantity = stockQuantity;
    }
}
