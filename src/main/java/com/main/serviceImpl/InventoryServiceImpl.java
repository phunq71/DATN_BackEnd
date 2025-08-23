package com.main.serviceImpl;

import com.main.dto.InventoryDTO;
import com.main.entity.Facility;
import com.main.entity.Inventory;
import com.main.entity.InventoryId;
import com.main.entity.Item;
import com.main.repository.InventoryRepository;
import com.main.service.InventoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;


    public Integer getQuantityByVariantAndSizeCode(String variantId, String code) {
        Integer qty = inventoryRepository.getQuantityByVariantAndSizeCode(variantId, code);
        return qty != null ? qty : 0;
    }

    @Override
    public Page<InventoryDTO> getInventoriesWareHouse(int page, String facilityID) {
        Pageable pageable = PageRequest.of(page, 7);
        return inventoryRepository.getInventoriesWareHouse(pageable, facilityID);
    }

    @Override
    public Page<InventoryDTO> getInventoriesShop(int page, String facilityID) {
        Pageable pageable = PageRequest.of(page, 7);
        return inventoryRepository.getInventoryDTO(pageable, facilityID);
    }

    @Override
    @Transactional
    public void updateMinMax(InventoryDTO inventoryDTO) {
        Inventory inventory = inventoryRepository.findById(new InventoryId(inventoryDTO.getItemId(), inventoryDTO.getFacilityId())).orElse(null);
        inventory.setMinQt(inventoryDTO.getMinShopQT());
        inventory.setMaxQt(inventoryDTO.getMaxShopQT());
        inventoryRepository.save(inventory);

        if(inventoryDTO.getStockId()!=null){
            Inventory invStock= inventoryRepository.findById(new InventoryId(inventoryDTO.getItemId(), inventoryDTO.getStockId())).orElse(null);
            invStock.setMinQt(inventoryDTO.getMinStockQT());
            invStock.setMaxQt(inventoryDTO.getMaxStockQT());
            inventoryRepository.save(invStock);
        }
    }
}
