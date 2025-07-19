package com.main.rest_controller;

import com.main.dto.SizeDTO;
import com.main.entity.Item;
import com.main.service.InventoryService;
import com.main.service.ItemService;
import com.main.service.SizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProductDetailRestController {
    private final InventoryService inventoryService;
    private final SizeService sizeService;

    public ProductDetailRestController(InventoryService inventoryService, SizeService sizeService) {
        this.inventoryService = inventoryService;
        this.sizeService = sizeService;
    }

    @GetMapping("/opulentia/variant/{variantID}/size/{sizeCode}")
        public ResponseEntity<?> getVariantSizeInfo(@PathVariable String variantID,
                                                    @PathVariable String sizeCode) {
            Integer variantInv = inventoryService.getQuantityByVariantAndSizeCode(variantID, sizeCode);
            return ResponseEntity.ok(variantInv);
        }

    @GetMapping("/opulentia/productDetail/checkSize/{variantID}")
    public ResponseEntity<List<SizeDTO>> getProductSizeInfo(@PathVariable String variantID) {
        List<SizeDTO> sizeDTOs = sizeService.getSizeDTOByVariantID(variantID);
        System.err.println(sizeDTOs);
        return ResponseEntity.ok(sizeDTOs);
    }
}
