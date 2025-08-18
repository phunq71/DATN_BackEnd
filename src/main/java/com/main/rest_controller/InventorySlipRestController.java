package com.main.rest_controller;

import com.main.dto.*;
import com.main.entity.InventorySlip;
import com.main.entity.Staff;
import com.main.repository.FacilityRepository;
import com.main.repository.InventorySlipRepository;
import com.main.service.InventorySlipService;
import com.main.service.MailService;
import com.main.utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class InventorySlipRestController {
    private final FacilityRepository facilityRepository;
    private final InventorySlipService inventorySlipService;
    private final MailService mailService;

    @GetMapping("/admin/inventorySlip/getAreas")
    public ResponseEntity<?> getAreas() {
        System.err.println("getAreas");
        String role = AuthUtil.getRole();
        String accountID = AuthUtil.getAccountID();
        Long badgeCount =0L;

        String facilityIDOfAccount = facilityRepository.getFacilityIdByStaffId(accountID);

        if(accountID ==null || role == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<AreaDTO> areas = new ArrayList<>();

        if (Objects.equals(role, "ROLE_ADMIN")) {
            areas = facilityRepository.getArea(null);
            badgeCount = inventorySlipService.getBageCount(null, "");
        } else if (Objects.equals(role, "ROLE_AREA")) {
            areas = facilityRepository.getArea(accountID);
            badgeCount = inventorySlipService.getBageCount(accountID,  "");
        } else if (Objects.equals(role, "ROLE_STOCK")) {
            String warehouseID = facilityRepository.getFacilityIdByStaffId(accountID);
            String ZID = "Z" + warehouseID.substring(1);
            areas = facilityRepository.getAreaDtoByAreaId(ZID);
            badgeCount = inventorySlipService.getBageCount(accountID, facilityIDOfAccount);
        } else if (Objects.equals(role, "ROLE_STAFF")) {
            String facilityId = facilityRepository.getFacilityIdByStaffId(accountID);
            areas.add(facilityRepository.getAreaDtoByFacilityId(facilityId));
            badgeCount = inventorySlipService.getBageCount(accountID, "K"+ facilityIDOfAccount.substring(1));
        }

        return ResponseEntity.ok(
                Map.of(
                        "areas", areas,
                "role", role.substring(5)
                ,"badgeCount", badgeCount
                , "facilityIDOfAccount", facilityIDOfAccount ==null?"":facilityIDOfAccount
                ));
    }

    @GetMapping("/admin/inventorySlip/pending")
    public ResponseEntity<List<InvSlipDTO>> getPendingSlips() {
        String role = AuthUtil.getRole();
        if(Objects.equals(role, "ROLE_ADMIN")){
            return ResponseEntity.ok(inventorySlipService.getPendingSlips(null, null));
        }

        String accountID = AuthUtil.getAccountID();
        if(Objects.equals(role, "ROLE_AREA")) {
            return ResponseEntity.ok(inventorySlipService.getPendingSlips(accountID, null));
        }

        String facilityIDOfAccount = facilityRepository.getFacilityIdByStaffId(accountID);


        return ResponseEntity.ok(
                inventorySlipService.getPendingSlips(accountID
                        ,Objects.equals(role, "ROLE_STAFF")
                                ? "K"+ facilityIDOfAccount.substring(1)
                                :facilityIDOfAccount));
    }

    @GetMapping("/admin/inventory/getFacilities")
    public ResponseEntity<?> getAllAreas() {
        String role = AuthUtil.getRole();
        String accountID = AuthUtil.getAccountID();
        String facilityIdOfAccount = facilityRepository.getFacilityIdByStaffId(accountID);
        List<AreaDTO> facilities = new ArrayList<>();
        if(Objects.equals(role, "ROLE_STOCK")){
            facilities = facilityRepository.getAllAreasDTO();
            facilities.removeIf(areaDTO -> Objects.equals(areaDTO.getShopID(), facilityIdOfAccount));

            facilities.addAll(facilityRepository.getAllWarehousesOfShop("Z"+ facilityIdOfAccount.substring(1)));
            return ResponseEntity.ok(Map.of(
                    "facilities", facilities
            ));
        }else if(Objects.equals(role, "ROLE_STAFF")){
            AreaDTO fac = facilityRepository.getAreaDtoByFacilityId(facilityIdOfAccount);
            facilities.add(fac);
            AreaDTO k = facilityRepository.getAreaDtoByFacilityId("K"+ facilityIdOfAccount.substring(1));
            facilities.add(k);
            return ResponseEntity.ok(Map.of(
                    "facilities", facilities
            ));
        } else if(Objects.equals(role, "ROLE_ADMIN")){
            facilities = facilityRepository.getAllAreasDTO();
            return ResponseEntity.ok(Map.of(
                    "facilities", facilities
            ));
        }

        return null;

    }

    @GetMapping("/admin/inventorySlip/get")
    public ResponseEntity<?> getInventorySlip(
            @RequestParam(value = "pageN", required = false) Integer pageN,
            @RequestParam(value = "date", required = false) LocalDate date,
            @RequestParam(value = "type", required = false) Boolean type,
            @RequestParam("facilityId") String facilityId
            ){
        String role = AuthUtil.getRole();
        String accountID = AuthUtil.getAccountID();
        String facilityIdOfAccount = facilityRepository.getFacilityIdByStaffId(accountID);

        List<String> facilityIds = new ArrayList<>();
        if(Objects.equals(role, "ROLE_ADMIN")) {
            if(facilityId.charAt(0) == 'Z') facilityIds.add("W" + facilityId.substring(1));
            else {
                facilityIds.add(facilityId);
                facilityIds.add("K" + facilityId.substring(1));
            }

        }else if(Objects.equals(role, "ROLE_AREA")) {
            if(facilityId.charAt(0) == 'Z'
                    && !facilityRepository.existsByFacilityIdAndManager_StaffID(facilityId, accountID)

            ) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            else if(facilityId.charAt(0) == 'S'
                    &&!facilityRepository.existsByFacilityIdAndParent_FacilityId(facilityId, facilityIdOfAccount)){
               return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }



            if(facilityId.charAt(0) == 'Z') facilityIds.add("W" + facilityId.substring(1));
            else {
                facilityIds.add(facilityId);
                facilityIds.add("K" + facilityId.substring(1));
            }
        }else if(Objects.equals(role, "ROLE_STOCK")){
            if(facilityId.charAt(0) == 'Z'
                    && !facilityRepository.existsByFacilityIdAndParent_FacilityId(facilityIdOfAccount, facilityId)
            ) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            else if(facilityId.charAt(0) == 'S'
                    &&!facilityRepository.existsByFacilityIdAndParent_FacilityId(facilityId,"Z"+ facilityIdOfAccount.substring(1))){

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            if(facilityId.charAt(0) == 'Z') facilityIds.add(facilityIdOfAccount);
            else {
                facilityIds.add(facilityId);
                facilityIds.add("K" + facilityId.substring(1));
            }
        }else if(Objects.equals(role, "ROLE_STAFF")){
            facilityIds.add(facilityIdOfAccount);
            facilityIds.add("K"+facilityIdOfAccount.substring(1));
        }

        if(pageN == null)pageN = 0;

        Page<InvSlipDTO> invSlipDTOS = inventorySlipService.getInventorySlipDTO(pageN,date,facilityIds,type);

        return ResponseEntity.ok(invSlipDTOS);
    }

    @GetMapping("/admin/inventorySlip/getItemInvSlips")
    public ResponseEntity<?> getInventorySlips(
            @RequestParam("pageN") int pageN,
            @RequestParam("keyword") String keyword,
            @RequestParam(value = "toFacilityId", required = false) String toFacilityId
    ){
        System.err.println("pageN: " + pageN);
        System.err.println("keyword: " + keyword);
        String facilityId = facilityRepository.getFacilityIdByStaffId(AuthUtil.getAccountID());

        if(Objects.equals(AuthUtil.getRole(), "ROLE_STAFF")
                && (!Objects.equals(toFacilityId, "") && toFacilityId.charAt(0) == 'S')
        )
        {
            facilityId ="K" + toFacilityId.substring(1);
        }
        Page<ItemInvSlipDTO> invSlipDTOs = inventorySlipService.getItemInvSlipDtos(pageN, keyword, facilityId);
        return ResponseEntity.ok(invSlipDTOs);
    }

    @PostMapping("/admin/inventorySlip/create")
    public ResponseEntity<?> createInventorySlip(
            @RequestBody InvSlipDTO invSlipDTO,
            @RequestParam boolean force
    ){
        System.err.println("invSlipDTO: " + invSlipDTO);

        String role = AuthUtil.getRole();

        if(!Objects.equals(role, "ROLE_STOCK")
                && !Objects.equals(role, "ROLE_STAFF")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if(!Objects.equals(role, "ROLE_STOCK") && invSlipDTO.getType()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if(Objects.equals(role, "ROLE_STAFF") && invSlipDTO.getTo()!=null && invSlipDTO.getTo().charAt(0) == 'W'){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String accountId = AuthUtil.getAccountID();

        String facilityIdOfAccount = facilityRepository.getFacilityIdByStaffId(accountId);
        if (Objects.equals(role, "ROLE_STAFF") && invSlipDTO.getTo()!=null && invSlipDTO.getTo().charAt(0) == 'S') {
            facilityIdOfAccount ="K"+ facilityIdOfAccount.substring(1);
        }

        if(     Objects.equals(role, "ROLE_STOCK")
                && invSlipDTO.getTo()!=null
                && invSlipDTO.getTo().charAt(0) == 'K'
                && !Objects.equals("Z"+ facilityIdOfAccount.substring(1), facilityRepository.getParentFacilityIdByShopId("S" + invSlipDTO.getTo().substring(1)))
        ){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if(invSlipDTO.getNote().length() >385){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }


        if(invSlipDTO.getType() && Objects.equals(role, "ROLE_STOCK")){
            invSlipDTO.setTo(facilityIdOfAccount);
        }

        if(invSlipDTO.getFrom()==null || invSlipDTO.getFrom().isEmpty()) {
            invSlipDTO.setFrom(facilityIdOfAccount);
        }



        invSlipDTO.setStaff(accountId);

        if(!invSlipDTO.getType()
                && !inventorySlipService.checkQuantityInventories(invSlipDTO.getFrom(), invSlipDTO.getDetails())
        ){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if(!force && invSlipDTO.getTo()!=null) {
            List<InvSlipDetailDTO> detailDTOs = inventorySlipService.checkQuantityLimit(invSlipDTO.getTo(), invSlipDTO.getDetails());
            if (detailDTOs != null && !detailDTOs.isEmpty()) {
                return ResponseEntity.ok(Map.of("status", "warning"
                        , "message", "Vượt max của kho nhận"
                        , "items", detailDTOs));
            }
        }



        InventorySlip inv =inventorySlipService.createInventorySlip(invSlipDTO);

        inventorySlipService.sendMail(inv);



        return ResponseEntity.ok(Map.of("status", "success"
                                , "message", "Tạo thành công!"));
    }


    @GetMapping("/admin/inventorySlip/approve")
    public ResponseEntity<?> approveInventorySlip(
            @RequestParam boolean isApproved,
            @RequestParam String invSlipId,
            @RequestParam String reason
    ) {
        try {
            String role = AuthUtil.getRole();
            if(!Objects.equals(role, "ROLE_ADMIN") && !Objects.equals(role, "ROLE_AREA")){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
            }

            InventorySlip invSlip = inventorySlipService.getInvSlipById(invSlipId);

            if(invSlip == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Inventory slip not found");
            }

            if(!"PENDING".equals(invSlip.getStatus())){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Slip is not pending");
            }

            String from = invSlip.getFromFacility() != null ? invSlip.getFromFacility().getType() : null;
            String to = invSlip.getToFacility() != null ? invSlip.getToFacility().getType() : null;

            if(("W".equals(from) && ("W".equals(to) || to == null)) && !"ROLE_ADMIN".equals(role)){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Only ADMIN can approve this slip");
            }

            if((("W".equals(from) && "K".equals(to)) ||
                    ("K".equals(from) && to == null) ||
                    ("S".equals(from) && to == null)) && !"ROLE_AREA".equals(role)){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Only AREA can approve this slip");
            }

            if(reason.length() > 100){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Reason too long (max 100 characters)");
            }

            if(!isApproved) {
                invSlip.setNote((invSlip.getNote() != null ? invSlip.getNote() : "")
                        + "[Lý do từ chối]" + reason);
            }

            invSlip.setApprover(new Staff(AuthUtil.getAccountID()));
            inventorySlipService.approveInvSlip(invSlip, isApproved);

            return ResponseEntity.ok("Approval successful");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi: " + e.getMessage());
        }
    }


    @PostMapping("/admin/inventorySlip/complete")
    public ResponseEntity<?> completeInventorySlip(
            @RequestBody InvSlipDTO invSlipDTO
        ){
        System.err.println("completeInventorySlip");
        System.err.println(invSlipDTO);
        String role = AuthUtil.getRole();

        if(!Objects.equals(role, "ROLE_STAFF") && !Objects.equals(role, "ROLE_STOCK")){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String accountId = AuthUtil.getAccountID();
        String facilityIdOfAccount = facilityRepository.getFacilityIdByStaffId(accountId);

        if( Objects.equals(role, "ROLE_STAFF")
            && !invSlipDTO.getTo().equals("K"+facilityIdOfAccount.substring(1))
        ){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if( Objects.equals(role, "ROLE_STOCK") &&!invSlipDTO.getTo().equals(facilityIdOfAccount)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        InventorySlip inventorySlip = inventorySlipService.getInvSlipById(invSlipDTO.getId());
        if(!Objects.equals("APPROVED", inventorySlip.getStatus())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        System.err.println("Bắt đầu tạo");
        invSlipDTO.setStaff(accountId);
        invSlipDTO.setApprover(null);
        invSlipDTO.setCreateDate(LocalDateTime.now());
        invSlipDTO.setType(true);
        inventorySlipService.createInventorySlip(invSlipDTO);
        System.err.println("Đã tạo");
        //cập nhật phiếu xuất thành công
        inventorySlipService.updateDoneExportSlip(invSlipDTO.getId());
        return ResponseEntity.ok("");
    }
}


