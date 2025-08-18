package com.main.service;

import com.main.dto.InventoryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;

public interface InventoryService {
      Integer getQuantityByVariantAndSizeCode(@Param("variantId") String variantId,
                                               @Param("code") String code);

      Page<InventoryDTO> getInventoriesWareHouse(int page, String facilityID);
      Page<InventoryDTO> getInventoriesShop(int page, String facilityID);

      void updateMinMax(InventoryDTO inventoryDTO);
}
