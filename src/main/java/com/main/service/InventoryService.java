package com.main.service;

import com.main.entity.Inventory;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface InventoryService {
      Integer getQuantityByVariantAndSizeCode(@Param("variantId") String variantId,
                                               @Param("code") String code);
}
