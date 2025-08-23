package com.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InventoryDTO {
    private Integer itemId;
    private String facilityId;
    private String sku;
    private Integer minShopQT;
    private Integer maxShopQT;
    private Integer minStockQT;
    private Integer maxStockQT;
    private Integer stockQuantity;
    private Integer shopQuantity;
    private String stockId;

    public InventoryDTO(Integer itemId, String facilityId, String sku, Integer minShopQT, Integer maxShopQT, Integer shopQuantity) {
        this.itemId = itemId;
        this.facilityId = facilityId;
        this.sku = sku;
        this.minShopQT = minShopQT;
        this.maxShopQT = maxShopQT;
        this.shopQuantity = shopQuantity;
    }

}
