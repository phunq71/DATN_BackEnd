package com.main.serviceImpl;

import com.main.dto.InvDTO_CheckLimitQT;
import com.main.dto.InvSlipDTO;
import com.main.dto.InvSlipDetailDTO;
import com.main.dto.ItemInvSlipDTO;
import com.main.entity.*;
import com.main.repository.*;
import com.main.service.InventorySlipService;
import com.main.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventorySlipServiceImpl implements InventorySlipService {
    private final InventorySlipRepository inventorySlipRepository;
    private final InventoryRepository inventoryRepository;
    private final StaffRepository staffRepository;
    private final FacilityRepository facilityRepository;
    private final AccountRepository accountRepository;

    private final MailService mailService;

    @Override
    public Page<InvSlipDTO> getInventorySlipDTO(int pageN, LocalDate date, List<String> facilityIds, Boolean type) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
        Pageable pageable = PageRequest.of(pageN, 6);

        Page<InvSlipDTO> invSlipDTOs = inventorySlipRepository.getInventorySlipDto(pageable, facilityIds, startOfDay, endOfDay, type);

        List<String> invSlipIds = invSlipDTOs.getContent().stream().map(InvSlipDTO::getId).toList();

        List<InvSlipDetailDTO> invSlipDetailDTOs= inventorySlipRepository.getInventorySlipDetailDto(invSlipIds);

        Map<String, List<InvSlipDetailDTO>> detailsMap = invSlipDetailDTOs.stream()
                .collect(Collectors.groupingBy(InvSlipDetailDTO::getInvSlipID));

        invSlipDTOs.getContent().forEach(invSlip ->
                invSlip.setDetails(detailsMap.getOrDefault(invSlip.getId(), List.of())));

        return invSlipDTOs;
    }

    @Override
    public Page<ItemInvSlipDTO> getItemInvSlipDtos(int pageN, String keyword, String facilityId) {
        Pageable pageable = PageRequest.of(pageN, 10);
        if(facilityId ==null || facilityId.isEmpty()){
            return inventorySlipRepository.getAllItemSlipDTO(pageable, keyword);
        }

        return inventorySlipRepository.getItemInvSlipDTO(pageable,facilityId, keyword);
    }

//    @Override
    public List<InvSlipDetailDTO> checkQuantityLimit2(String facilityId, List<InvSlipDetailDTO> details) {
        // Lấy danh sách itemIDs từ details
        List<Integer> itemIds = details.stream().map(InvSlipDetailDTO::getItemID).toList();

        // Lấy thông tin giới hạn tồn kho từ repository
        List<InvDTO_CheckLimitQT> limits = inventoryRepository.getInventoryByItemIdsAndFacilityId(itemIds, facilityId);
        System.err.println("limits: " + limits);
        // Tạo map để truy cập nhanh thông tin giới hạn theo itemId
        Map<Integer, InvDTO_CheckLimitQT> limitMap = limits.stream()
                .collect(Collectors.toMap(InvDTO_CheckLimitQT::getItemId, Function.identity()));

        // Tạo list kết quả rỗng
        List<InvSlipDetailDTO> result = new ArrayList<>();

        for (InvSlipDetailDTO detail : details) {
            // Tạo bản sao của detail để tránh thay đổi dữ liệu gốc
            InvSlipDetailDTO newDetail = new InvSlipDetailDTO();
            newDetail.setInvSlipID(detail.getInvSlipID());
            newDetail.setItemID(detail.getItemID());
            newDetail.setItemName(detail.getItemName());
            newDetail.setQuantity(detail.getQuantity());
            newDetail.setMaxQT(detail.getMaxQT());

            // Lấy thông tin giới hạn cho item hiện tại
            InvDTO_CheckLimitQT limit = limitMap.get(detail.getItemID());

            if (limit != null) {
                // Tính tổng quantity (hiện có + quantity trong phiếu)
                int totalQuantity = limit.getQuantity() + detail.getQuantity();

                // Nếu vượt quá giới hạn, cập nhật maxQT
                if (totalQuantity > limit.getMaxQT()) {
                    newDetail.setMaxQT(limit.getMaxQT());
                    newDetail.setInvSlipID(limit.getQuantity()+""); //Dùng tạm thuộc tính này để lưu số lượng hiện tại của kho đỡ tạo thêm thuộc tính khác
                }
            }

            // Thêm vào danh sách kết quả
            result.add(newDetail);
        }
        System.err.println("result: " + result  );
        return result;
    }

    @Override
    public List<InvSlipDetailDTO> checkQuantityLimit(String facilityId, List<InvSlipDetailDTO> details) {
        // 1️⃣ Lấy danh sách itemIDs từ details
        List<Integer> itemIds = details.stream().map(InvSlipDetailDTO::getItemID).toList();
        System.err.println("1️⃣ itemIds: " + itemIds);

        // 2️⃣ Lấy thông tin giới hạn tồn kho từ repository
        List<InvDTO_CheckLimitQT> limits = inventoryRepository.getInventoryByItemIdsAndFacilityId(itemIds, facilityId);
        System.err.println("2️⃣ limits (giới hạn từ DB): " + limits);

        // 3️⃣ Tạo map để truy cập nhanh thông tin giới hạn theo itemId
        Map<Integer, InvDTO_CheckLimitQT> limitMap = limits.stream()
                .collect(Collectors.toMap(InvDTO_CheckLimitQT::getItemId, Function.identity()));
        System.err.println("3️⃣ limitMap: " + limitMap);

        // 4️⃣ Tạo list kết quả rỗng
        List<InvSlipDetailDTO> result = new ArrayList<>();

        for (InvSlipDetailDTO detail : details) {
            // 5️⃣ Tạo bản sao của detail
            InvSlipDetailDTO newDetail = new InvSlipDetailDTO();
            newDetail.setInvSlipID(detail.getInvSlipID());
            newDetail.setItemID(detail.getItemID());
            newDetail.setItemName(detail.getItemName());
            newDetail.setQuantity(detail.getQuantity());
            newDetail.setMaxQT(detail.getMaxQT());

            System.err.println("5️⃣ Đang xử lý itemID=" + detail.getItemID() +
                    ", name=" + detail.getItemName() +
                    ", quantity=" + detail.getQuantity());

            // 6️⃣ Lấy giới hạn tồn kho của item
            InvDTO_CheckLimitQT limit = limitMap.get(detail.getItemID());
            System.err.println("6️⃣ Giới hạn cho itemID " + detail.getItemID() + ": " + limit);

            if (limit != null) {
                // 7️⃣ Tính tổng quantity
                int totalQuantity = limit.getQuantity() + detail.getQuantity();
                System.err.println("7️⃣ totalQuantity = tồn kho hiện tại (" + limit.getQuantity() +
                        ") + nhập thêm (" + detail.getQuantity() +
                        ") = " + totalQuantity);

                // 8️⃣ Nếu vượt maxQT thì cập nhật maxQT
                if (totalQuantity > limit.getMaxQT()) {
                    System.err.println("8️⃣ ⚠️ Vượt maxQT (" + limit.getMaxQT() + ")");
                    newDetail.setMaxQT(limit.getMaxQT());
                    newDetail.setInvSlipID(limit.getQuantity() + "");
                    // 9️⃣ Thêm vào danh sách kết quả
                    result.add(newDetail);
                } else {
                    System.err.println("8️⃣ ✅ Không vượt maxQT");
                }
            }


        }

        // 🔟 In kết quả cuối cùng
        System.err.println("🔟 result: " + result);
        return result;
    }

    @Override
    public boolean checkQuantityInventories(String facilityId, List<InvSlipDetailDTO> details) {
        System.err.println("kiểm tra số lượng: "+ facilityId);
        List<Integer> itemIds = details.stream().map(InvSlipDetailDTO::getItemID).toList();
        List<InvDTO_CheckLimitQT> limits = inventoryRepository.getInventoryByItemIdsAndFacilityId(itemIds, facilityId);

        Map<Integer, InvDTO_CheckLimitQT> mapLimits = limits.stream().collect(Collectors.toMap(InvDTO_CheckLimitQT::getItemId, Function.identity()));

        for (InvSlipDetailDTO detail : details) {
            InvDTO_CheckLimitQT limit = mapLimits.get(detail.getItemID());
            if (detail.getQuantity() > limit.getQuantity()) {
                System.err.println("số lượng của "+detail.getItemID()+ ": " + detail.getQuantity());
                System.err.println("limit: " + limit.getQuantity());
                return false;
            }
        }

        return true;
    }


    @Override
    @Transactional
    public InventorySlip createInventorySlip(InvSlipDTO dto) {
        InventorySlip slip = buildInventorySlipFromDTO(dto);
        boolean isStaffCreated = isStaffCreatedSlip(dto);

        slip.setStatus(isStaffCreated || dto.getType() ? "DONE"  : "PENDING");
        slip.setInventorySlipDetails(buildSlipDetailsAndUpdateInventory(dto, slip, isStaffCreated));

        InventorySlip slip1 =inventorySlipRepository.save(slip);

        if (isStaffCreated) {
            InventorySlip slip2 = createMatchingImportSlip(slip);
            inventorySlipRepository.save(slip2);
        }

        return slip1;
    }

    @Override
    public Long getBageCount(String managerId, String facilityId) {
        if(facilityId==null || facilityId.isEmpty()) {
            if (managerId == null || managerId.isEmpty()) {
                return inventorySlipRepository.countPendingSlipOfAdmin();
            }
            return inventorySlipRepository.countPendingSlipOfArea(managerId);
        }

        return inventorySlipRepository.countApprovedSlips(facilityId);
    }

    @Override
    public List<InvSlipDTO> getPendingSlips(String managerId, String facilityId) {
        List<InvSlipDTO> result = new ArrayList<>();
        if(facilityId ==null || facilityId.isEmpty()) {
            if (managerId == null || managerId.isEmpty()) {
                result = inventorySlipRepository.getPendingSlipOfAdmin();
            } else {
                result = inventorySlipRepository.getPendingSlipOfArea(managerId);
            }
        }else {
            result = inventorySlipRepository.getApprovedSlips(facilityId);
        }
        List<String> invSlipIds = result.stream().map(InvSlipDTO::getId).toList();

        List<InvSlipDetailDTO> invSlipDetailDTOs= inventorySlipRepository.getInventorySlipDetailDto(invSlipIds);

        Map<String, List<InvSlipDetailDTO>> detailsMap = invSlipDetailDTOs.stream()
                .collect(Collectors.groupingBy(InvSlipDetailDTO::getInvSlipID));

        result.forEach(invSlip ->
                invSlip.setDetails(detailsMap.getOrDefault(invSlip.getId(), List.of())));

        return result;
    }

    @Override
    public InventorySlip getInvSlipById(String id) {
        return inventorySlipRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void approveInvSlip(InventorySlip inventorySlip, boolean isApproved) {
        try {
            inventorySlip.setStatus(isApproved ? "APPROVED" : "REJECTED");

            if(isApproved && inventorySlip.getToFacility() == null) {
                inventorySlip.setStatus("DONE");
            }

            inventorySlipRepository.save(inventorySlip);

            if (isApproved) {
                List<Inventory> inventories = new ArrayList<>();
                for (InventorySlipDetail detail : inventorySlip.getInventorySlipDetails()) {
                    InventoryId invId = new InventoryId(
                            detail.getItem().getItemId(),
                            inventorySlip.getFromFacility().getFacilityId()
                    );

                    Inventory inv = inventoryRepository.findById(invId)
                            .orElseThrow(() -> new RuntimeException("Inventory not found for item " + detail.getItem().getItemId()));

                    int newQty = inv.getQuantity() - detail.getQuantity();
                    if (newQty < 0) {
                        throw new RuntimeException("Tồn kho không đủ của item " + detail.getItem().getItemId());
                    }

                    inv.setQuantity(newQty);
                    inventories.add(inv);
                }
                inventoryRepository.saveAll(inventories);
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi chấp nhận phiếu: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void updateDoneExportSlip(String id) {
        inventorySlipRepository.updateDoneInvSlip(id);
    }

    @Override
    public void sendMail(InventorySlip inventorySlip) {
        Facility from = inventorySlip.getFromFacility();

        if (from == null) {
            return;
        }

        List<String> toEmails = new ArrayList<>();
        Facility to = inventorySlip.getToFacility();

        if (to == null) {
            if ("W".equals(from.getType())) {
                // gửi admin
                toEmails = accountRepository.getEmailAdmin();
            } else if ("S".equals(from.getType()) || "K".equals(from.getType())) {
                // gửi area
                String facilityId = "S" + from.getFacilityId().substring(1);
                String parentFacilityId = facilityRepository.getParentFacilityIdByShopId(facilityId);
                String emailArea = accountRepository.getEmailArea(parentFacilityId);
                toEmails.add(emailArea);
            }
        } else {
            if ("W".equals(from.getType()) && "W".equals(to.getType())) {
                // gửi admin
                toEmails = accountRepository.getEmailAdmin();
            } else if ("W".equals(from.getType()) && "K".equals(to.getType())) {
                // gửi area
                String facilityId = "S" + from.getFacilityId().substring(1);
                String parentFacilityId = facilityRepository.getParentFacilityIdByShopId(facilityId);
                String emailArea = accountRepository.getEmailArea(parentFacilityId);
                toEmails.add(emailArea);
            }
        }

        if (!toEmails.isEmpty()) {
            System.err.println("Chuẩn bị gửi mail cho: "+toEmails);
            mailService.sendSlipNotificationEmail(toEmails, "Xuất", inventorySlip.getIsid(), "duyệt");
            System.err.println("đã gửi");
        }
    }



    private String generateInvSlipId(boolean type) {
        String prefix = type ? "N" : "X";

        // Tìm ID lớn nhất hiện có với prefix tương ứng
        String maxId = inventorySlipRepository.findMaxInvSlipIdByPrefix(prefix);

        int nextNumber = 1; // Mặc định nếu chưa có ID nào

        if (maxId != null && maxId.startsWith(prefix)) {
            try {
                String numberStr = maxId.substring(1);
                nextNumber = Integer.parseInt(numberStr) + 1;
            } catch (NumberFormatException e) {
                // Nếu có lỗi khi parse số, giữ nguyên giá trị mặc định
            }
        }

        // Format số với 9 chữ số, thêm leading zeros
        String numberPart = String.format("%09d", nextNumber);

        return prefix + numberPart;
    }

    //Các phương thức bổ trợ create Slip
    /**
     * Tạo đối tượng InventorySlip từ dữ liệu DTO.
     *
     * @param dto Dữ liệu phiếu xuất/nhập từ client.
     * @return InventorySlip đã được set đầy đủ thông tin (trừ details).
     */
    private InventorySlip buildInventorySlipFromDTO(InvSlipDTO dto) {
        InventorySlip slip = new InventorySlip();
        slip.setIsid(generateInvSlipId(dto.getType()));
        slip.setCreateDate(LocalDateTime.now());
        slip.setNote(dto.getNote());
        slip.setStaff(staffRepository.getByStaffID(dto.getStaff()));
        slip.setType(dto.getType());

        if (dto.getFrom() != null && !dto.getFrom().isEmpty()) {
            slip.setFromFacility(facilityRepository.findById(dto.getFrom()).orElse(null));
        }
        if(dto.getTo() != null) {
            slip.setToFacility(facilityRepository.findById(dto.getTo()).orElse(null));
        }
        return slip;
    }

    /**
     * Kiểm tra phiếu có được tạo bởi nhân viên hay không.
     * Điều kiện:
     * - Type = false (phiếu xuất)
     * - From & To phải là cặp K→S hoặc S→K
     *
     * @param dto Dữ liệu phiếu xuất/nhập từ client.
     * @return true nếu là nhân viên tạo phiếu, ngược lại false.
     */
    private boolean isStaffCreatedSlip(InvSlipDTO dto) {
        return !dto.getType() && dto.getTo()!=null && (
                (dto.getFrom().charAt(0) == 'K' && dto.getTo().charAt(0) == 'S')
                        || (dto.getFrom().charAt(0) == 'S' && dto.getTo().charAt(0) == 'K')
        );
    }


    /**
     * Tạo danh sách InventorySlipDetail từ DTO và cập nhật tồn kho.
     *
     * @param dto Dữ liệu phiếu xuất/nhập từ client.
     * @param slip Phiếu đã được khởi tạo (chưa có details).
     * @param isStaffCreated true nếu là nhân viên tạo phiếu.
     * @return Danh sách InventorySlipDetail đã tạo.
     */
    private List<InventorySlipDetail> buildSlipDetailsAndUpdateInventory(
            InvSlipDTO dto, InventorySlip slip, boolean isStaffCreated) {

        List<InventorySlipDetail> details = new ArrayList<>();

        dto.getDetails().forEach(detailDTO -> {
            InventorySlipDetail invDetail = new InventorySlipDetail();
            invDetail.setInventorySlip(slip);
            invDetail.setItem(new Item(detailDTO.getItemID()));
            invDetail.setId(new InventorySlipDetailId(detailDTO.getItemID(), slip.getIsid()));
            invDetail.setQuantity(detailDTO.getQuantity());
            details.add(invDetail);

            if (isStaffCreated || dto.getType()) {
                updateInventoryQuantity(dto, detailDTO);
            }
        });

        return details;
    }



    /**
     * Cập nhật số lượng tồn kho cho From và To dựa trên chi tiết phiếu.
     * Nếu inventory chưa tồn tại thì tạo mới.
     *
     * @param dto Dữ liệu phiếu xuất/nhập từ client.
     * @param detailDTO Chi tiết sản phẩm trong phiếu.
     */
    private void updateInventoryQuantity(InvSlipDTO dto, InvSlipDetailDTO detailDTO) {
        InventoryId inventoryToId = new InventoryId(detailDTO.getItemID(), dto.getTo());

        if (inventoryRepository.existsById(inventoryToId)) {
            inventoryRepository.updateQuantity(inventoryToId, detailDTO.getQuantity());
        } else {
            inventoryRepository.save(
                    new Inventory(
                            inventoryToId,
                            new Item(detailDTO.getItemID()),
                            new Facility(dto.getTo()),
                            detailDTO.getQuantity(),
                            1,
                            100
                    )
            );
        }

        if (dto.getFrom() != null && !dto.getFrom().isEmpty()) {
            InventoryId inventoryFromId = new InventoryId(detailDTO.getItemID(), dto.getFrom());
            inventoryRepository.updateQuantity(inventoryFromId, -detailDTO.getQuantity());
        }
    }


    /**
     * Tạo phiếu nhập tương ứng khi nhân viên tạo phiếu xuất.
     *
     * @param originalSlip Phiếu gốc vừa được tạo.
     * @return Phiếu nhập tương ứng với phiếu xuất gốc.
     */
    private InventorySlip createMatchingImportSlip(InventorySlip originalSlip) {
        InventorySlip slip2 = new InventorySlip();
        slip2.setInventorySlipDetails(new ArrayList<>());
        slip2.setIsid(generateInvSlipId(true));
        slip2.setType(true);
        slip2.setFromFacility(originalSlip.getFromFacility());
        slip2.setToFacility(originalSlip.getToFacility());
        slip2.setStatus(originalSlip.getStatus());
        slip2.setCreateDate(originalSlip.getCreateDate());
        slip2.setStaff(originalSlip.getStaff());
        slip2.setApprover(originalSlip.getApprover());
        slip2.setNote(originalSlip.getNote());

        originalSlip.getInventorySlipDetails().forEach(detail -> {
            InventorySlipDetail invDetail = new InventorySlipDetail();
            invDetail.setInventorySlip(slip2);
            invDetail.setItem(detail.getItem());
            invDetail.setQuantity(detail.getQuantity());
            invDetail.setId(new InventorySlipDetailId(detail.getItem().getItemId(), slip2.getIsid()));
            slip2.getInventorySlipDetails().add(invDetail);
        });

        return slip2;
    }



    @Scheduled(fixedRate = 3600000) // chạy mỗi 1 tiếng
    public void autoRejectSlips() {
        System.err.println("Auto Reject Slips");
        LocalDateTime limit = LocalDateTime.now().minusHours(24);
        inventorySlipRepository.rejectOldSlips(limit);
    }


}
