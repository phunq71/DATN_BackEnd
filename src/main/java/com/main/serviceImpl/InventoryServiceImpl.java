package com.main.serviceImpl;

import com.main.entity.Inventory;
import com.main.repository.InventoryRepository;
import com.main.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InventoryServiceImpl implements InventoryService {
    @Autowired
    private InventoryRepository inventoryRepository;
    public Integer getQuantityByVariantAndSizeCode(String variantId, String code) {
        Integer qty = inventoryRepository.getQuantityByVariantAndSizeCode(variantId, code);
        return qty != null ? qty : 0;
    }
}
