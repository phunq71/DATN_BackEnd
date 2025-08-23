package com.main.rest_controller;

import com.main.dto.AreaDTO;
import com.main.dto.InventoryDTO;
import com.main.repository.FacilityRepository;
import com.main.repository.StaffRepository;
import com.main.service.InventoryService;
import com.main.service.MailService;
import com.main.utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class InventoryRestController {
    private final InventoryService inventoryService;
    private final StaffRepository staffRepository;
    private final FacilityRepository facilityRepository;

    private final MailService mailService;

    @GetMapping("/admin/inventory/getAreas")
    public ResponseEntity<List<AreaDTO>> getAreas() {
        System.err.println("getAreas");
        String role = AuthUtil.getRole();
        String accountID = AuthUtil.getAccountID();
        List<AreaDTO> areas = new ArrayList<>();

        if (Objects.equals(role, "ROLE_ADMIN")) {
            areas = facilityRepository.getArea(null);
        } else if (Objects.equals(role, "ROLE_AREA")) {
            areas = facilityRepository.getArea(accountID);
        } else if (Objects.equals(role, "ROLE_STOCK")) {
            String warehouseID = facilityRepository.getFacilityIdByStaffId(accountID);
            String ZID = "Z" + warehouseID.substring(1);
            areas = facilityRepository.getAreaDtoByAreaId(ZID);
        } else if (Objects.equals(role, "ROLE_STAFF")) {
            String facilityId = facilityRepository.getFacilityIdByStaffId(accountID);
            areas.add(facilityRepository.getAreaDtoByFacilityId(facilityId));
        }

        return ResponseEntity.ok(areas);
    }

    @GetMapping("/admin/inventory/getInventories")
    public ResponseEntity<Map<String, Object>> getInventories(
            @RequestParam(value = "facilityId", required = false) String facilityId,
            @RequestParam(value = "pageN", required = false) Integer pageN) {
        String role = AuthUtil.getRole();
        String accountID = AuthUtil.getAccountID();

        if (pageN == null || pageN < 0) pageN = 0;

        Page<InventoryDTO> inventoryDTOs = Page.empty();

        if (Objects.equals(role, "ROLE_ADMIN")) {
            List<AreaDTO> areas = facilityRepository.getArea(null);
            if (facilityId == null && !areas.isEmpty()) {
                inventoryDTOs = inventoryService.getInventoriesWareHouse(pageN, "W"+areas.get(0).getAreaID().substring(1));
            }
            else if (facilityId != null && facilityId.charAt(0) == 'Z') {
                inventoryDTOs = inventoryService.getInventoriesWareHouse(pageN, "W"+facilityId.substring(1));
            } else if (facilityId != null && facilityId.charAt(0) == 'S') {
                inventoryDTOs = inventoryService.getInventoriesShop(pageN, facilityId);
            }
        } else if (Objects.equals(role, "ROLE_AREA")) {
            List<AreaDTO> areas = facilityRepository.getArea(accountID);
            if (facilityId != null
                    && !facilityRepository.existsByFacilityIdAndManager_StaffID(facilityId, accountID)
                    && !isShopBelongToAreas(areas, facilityId)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String facilityIdByManagerId = facilityRepository.getFacilityIdByManagerId(accountID);
            if (facilityId == null || facilityId.equals(facilityIdByManagerId)) {
                String warehouseID = "W" + facilityIdByManagerId.substring(1);
                inventoryDTOs = inventoryService.getInventoriesWareHouse(pageN, warehouseID);
            } else {
                inventoryDTOs = inventoryService.getInventoriesShop(pageN, facilityId);
            }
        } else if (Objects.equals(role, "ROLE_STOCK")) {
            String warehouseID = facilityRepository.getFacilityIdByStaffId(accountID);
            String ZID = "Z" + warehouseID.substring(1);
            List<AreaDTO> areas = facilityRepository.getAreaDtoByAreaId(ZID);

            if (facilityId != null && !facilityId.equals(ZID) && !isShopBelongToAreas(areas, facilityId)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            if (facilityId == null || facilityId.equals(ZID)) {
                inventoryDTOs = inventoryService.getInventoriesWareHouse(pageN, warehouseID);
            } else {
                inventoryDTOs = inventoryService.getInventoriesShop(pageN, facilityId);
            }
        } else if (Objects.equals(role, "ROLE_STAFF")) {
            facilityId = facilityRepository.getFacilityIdByStaffId(accountID);
            inventoryDTOs = inventoryService.getInventoriesShop(pageN, facilityId);
        }

        return ResponseEntity.ok(Map.of(
                "inventories", inventoryDTOs,
                "role", role
        ));
    }

    @PutMapping("/admin/inventory/update")
    public ResponseEntity<?> updateInventory(
            @RequestParam("areaId") String areaId,
            @RequestBody InventoryDTO inventoryDTO
    ){
        String role = AuthUtil.getRole();
        String accountID = AuthUtil.getAccountID();

        if (
            (!Objects.equals(role, "ROLE_ADMIN") && !Objects.equals(role, "ROLE_AREA")) ||
            (Objects.equals(role, "ROLE_AREA") && facilityRepository.existsByFacilityIdAndManager_StaffID(areaId, accountID))
        ) {
            System.err.println("Không có quyền truy cập");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        inventoryService.updateMinMax(inventoryDTO);

        return ResponseEntity.ok("");
    }



    private boolean isShopBelongToAreas(List<AreaDTO> areas, String shopID) {
        if (areas == null || shopID == null) {
            return false;
        }
        return areas.stream()
                .anyMatch(area -> shopID.equals(area.getShopID()));
    }

}
