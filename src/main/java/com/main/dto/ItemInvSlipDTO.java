package com.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemInvSlipDTO {
    private Integer itemId;
    private String sku;
    private Integer quantity;

    public ItemInvSlipDTO(Integer itemId, String sku) {
        this.itemId = itemId;
        this.sku = sku;
    }
}
