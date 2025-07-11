package com.main.rest_controller;

import com.main.entity.Item;
import com.main.service.InventoryService;
import com.main.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProductDetailRestController {
    @Autowired
    InventoryService inventoryService;
        @GetMapping("/opulentia/variant/{variantID}/size/{sizeCode}")
        public ResponseEntity<?> getVariantSizeInfo(@PathVariable String variantID,
                                                    @PathVariable String sizeCode) {
            System.out.println(variantID);
            System.out.println(sizeCode);
            Integer variantInv = inventoryService.getQuantityByVariantAndSizeCode(variantID, sizeCode);
            System.out.println(variantInv);
            return ResponseEntity.ok(variantInv);
        }
}
