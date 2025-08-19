package com.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InvSlipDetailDTO {
    private String invSlipID;
    private Integer itemID;
    private String itemName;
    private Integer quantity;
    private Integer maxQT;
    public InvSlipDetailDTO(String invSlipID, Integer itemID, String sku, Integer quantity) {
        this.invSlipID = invSlipID;
        this.itemID = itemID;
        this.itemName = sku;
        this.quantity = quantity;
    }
}
