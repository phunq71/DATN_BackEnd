package com.main.serviceImpl;
import com.main.dto.FacilityDTO;
import com.main.dto.FacilityOrdManagerDTO;
import com.main.dto.FacilityOrderDTO;
import com.main.dto.OrderPreviewDTO;
import com.main.entity.*;
import com.main.mapper.FacilityMapper;
import com.main.repository.*;
import com.main.service.FacilityService;
import com.main.utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FacilityServiceImpl implements FacilityService {

    private final FacilityRepository facilityRepository;

    private final OrderRepository orderRepository;

    private final InventoryRepository inventoryRepository;

    private final StaffRepository staffRepository;

    // lấy các cơ sở tham gia bán online và đủ hàng -> gửi qua Fe xử lý
    @Override
    public List<FacilityOrderDTO> getAllFacilities(List<OrderPreviewDTO> items) {
        List<Facility> facilities = facilityRepository.getFacilityByIsUseTrue();
        List<FacilityOrderDTO> list = new ArrayList<>();

        for (Facility facility : facilities) {
            boolean flag = true; // ✅ Reset lại mỗi lần xét 1 kho

            for (OrderPreviewDTO item : items) {
                InventoryId inventoryId = new InventoryId(item.getItem_id(), facility.getFacilityId());
                Inventory inventory = inventoryRepository.getInventoryById(inventoryId);

                if (inventory == null || inventory.getQuantity() < item.getQuantity()) {
                    flag = false;
                    break; // ❌ Không đủ hàng thì bỏ qua luôn
                }
            }

            if (flag) {
                FacilityOrderDTO facilityOrderDTO = new FacilityOrderDTO();
                facilityOrderDTO.setId(facility.getFacilityId());
                facilityOrderDTO.setTotalOrders(orderRepository.countProcessingOrders(facility.getFacilityId()));
                facilityOrderDTO.setAddress(facility.getAddress());
                facilityOrderDTO.setAddressIdGHN(facility.getAddressIdGHN());
                list.add(facilityOrderDTO);
            }
        }

        return list;
    }

    @Override
    public List<FacilityOrdManagerDTO> getShop() {
        return facilityRepository.getShop();
    }

    @Override
    public List<FacilityOrdManagerDTO> getShopByManager_ID(String managerID) {
        return facilityRepository.getShopByManager_ID(managerID);
    }

    @Override
    public List<FacilityOrdManagerDTO> getShopByStaffID(String staffId) {
        return facilityRepository.getShopByStaffID(staffId);
    }

    @Override
    public List<FacilityDTO> getAllFacilities() {
        List<Facility> facilities = facilityRepository.findAll();
        return facilities.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public FacilityDTO getFacilityById(String facilityId) {
        Facility facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> new RuntimeException("Facility not found"));
        return mapToDTO(facility);
    }

    @Override
    public FacilityDTO createFacility(FacilityDTO dto) {
        Facility facility = new Facility();

        // --- Generate FacilityID ---
        String prefix = dto.getType(); // Z, W, S, K
        String lastId = facilityRepository.findTopByTypeOrderByFacilityIdDesc(prefix)
                .map(Facility::getFacilityId)
                .orElse(null);

        int number = 1;
        if (lastId != null) {
            // bỏ ký tự prefix ở đầu, parse phần số
            number = Integer.parseInt(lastId.substring(1)) + 1;
        }
        String newId = prefix + String.format("%05d", number);
        facility.setFacilityId(newId);

        facility.setType(dto.getType());
        facility.setFacilityName(dto.getFacilityName());
        facility.setAddress(dto.getAddress());
        facility.setIsUse(dto.getIsUse() != null ? dto.getIsUse() : true);

        if (dto.getParentId() != null) {
            Facility parent = facilityRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent facility not found"));
            facility.setParent(parent);
        }
        facility.setAddressIdGHN(dto.getAddressIdGHN());
        // --- Rule đặc biệt ---
        if ("Z".equals(dto.getType())) {
            if (dto.getManagerId() == null) {
                throw new RuntimeException("Khu vực phải có 1 quản lý");
            }
            Staff manager = staffRepository.findById(dto.getManagerId())
                    .orElseThrow(() -> new RuntimeException("Manager not found"));
            facility.setManager(manager);
        }

        if ("W".equals(dto.getType())) {
            if (facility.getParent() == null || !"Z".equals(facility.getParent().getType())) {
                throw new RuntimeException("Kho khu vực phải thuộc về một khu vực");
            }
            boolean exists = facilityRepository.existsByTypeAndParent("W", facility.getParent());
            if (exists) {
                throw new RuntimeException("Mỗi khu vực chỉ được có 1 kho khu vực");
            }
        }

        facilityRepository.save(facility);
        return mapToDTO(facility);
    }


    @Override
    public FacilityDTO updateFacility(String facilityId, FacilityDTO dto) {
        Facility facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> new RuntimeException("Facility not found"));

        // Không cho đổi type
        if (!facility.getType().equals(dto.getType())) {
            throw new RuntimeException("Không được phép thay đổi loại cơ sở");
        }

        facility.setFacilityName(dto.getFacilityName());
        facility.setAddress(dto.getAddress());
        if (dto.getIsUse() != null) {
            facility.setIsUse(dto.getIsUse());
        }

        if (dto.getParentId() != null) {
            Facility parent = facilityRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent facility not found"));
            facility.setParent(parent);
        }
        facility.setAddressIdGHN(dto.getAddressIdGHN());

        if ("Z".equals(facility.getType())) {
            // Update manager nếu có
            if (dto.getManagerId() != null) {
                Staff manager = staffRepository.findById(dto.getManagerId())
                        .orElseThrow(() -> new RuntimeException("Manager not found"));
                facility.setManager(manager);
            }
        }

        facilityRepository.save(facility);
        return mapToDTO(facility);
    }

    @Override
    public void moveFacility(String facilityId, String toParentId) {
        Facility facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> new RuntimeException("Facility not found"));

        Facility newParent = facilityRepository.findById(toParentId)
                .orElseThrow(() -> new RuntimeException("Parent facility not found"));

        // --- Rule validate ---
        if (facility.getFacilityId().equals(toParentId)) {
            throw new RuntimeException("Không thể di chuyển cơ sở vào chính nó");
        }
        if ("Z".equals(facility.getType())) {
            throw new RuntimeException("Không thể di chuyển khu vực");
        }
        if ("W".equals(facility.getType()) && !"Z".equals(newParent.getType())) {
            throw new RuntimeException("Kho khu vực chỉ được thuộc về khu vực");
        }
        if ("S".equals(facility.getType()) && !"Z".equals(newParent.getType())) {
            throw new RuntimeException("Shop chỉ được thuộc về khu vực");
        }
        if ("K".equals(facility.getType()) && !"S".equals(newParent.getType())) {
            throw new RuntimeException("Kho của shop chỉ được thuộc về shop");
        }

        facility.setParent(newParent);
        facilityRepository.save(facility);
    }

    @Override
    public FacilityDTO deleteOrDeactivateFacility(String facilityId) {
        Facility facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> new RuntimeException("Facility not found"));

        boolean hasInventory = inventoryRepository.existsByFacilityAndQuantityGreaterThan(facility, 0);
        boolean hasStaff = staffRepository.existsByFacility(facility);
        boolean hasUnfinishedOrders = orderRepository.existsByFacilityAndStatusNot(facility, "DaGiao");

        // Nếu là shop → phải check cả kho con (K)
        if ("S".equals(facility.getType())) {
            List<Facility> subWarehouses = facilityRepository.findByParent(facility);
            for (Facility sub : subWarehouses) {
                if ("K".equals(sub.getType())) {
                    if (inventoryRepository.existsByFacilityAndQuantityGreaterThan(sub, 0)) {
                        hasInventory = true;
                    }
                    if (orderRepository.existsByFacilityAndStatusNot(sub, "DaGiao")) {
                        hasUnfinishedOrders = true;
                    }
                }
            }
        }

        FacilityDTO result = mapToDTO(facility);

        if (hasInventory) {
            facility.setIsUse(false);
            facilityRepository.save(facility);
            result.setDeleteMessage("Không thể xóa cơ sở vì còn tồn kho, cơ sở đã bị tắt hoạt động.");
            result.setIsUse(false);
            return result;
        }

        if (hasStaff) {
            facility.setIsUse(false);
            facilityRepository.save(facility);
            result.setDeleteMessage("Không thể xóa cơ sở vì còn nhân viên, cơ sở đã bị tắt hoạt động.");
            result.setIsUse(false);
            return result;
        }

        if (hasUnfinishedOrders) {
            facility.setIsUse(false);
            facilityRepository.save(facility);
            result.setDeleteMessage("Không thể xóa cơ sở vì còn đơn hàng chưa hoàn tất, cơ sở đã bị tắt hoạt động.");
            result.setIsUse(false);
            return result;
        }

        // Nếu không có ràng buộc thì xóa hẳn
        facilityRepository.delete(facility);
        result.setDeleteMessage("Đã xóa cơ sở thành công.");
        return result;
    }


    private FacilityDTO mapToDTO(Facility facility) {
        FacilityDTO dto = new FacilityDTO();
        dto.setFacilityId(facility.getFacilityId());
        dto.setType(facility.getType());
        dto.setFacilityName(facility.getFacilityName());
        dto.setAddress(facility.getAddress());
        dto.setIsUse(facility.getIsUse());
        dto.setParentId(facility.getParent() != null ? facility.getParent().getFacilityId() : null);
        dto.setAddressIdGHN(facility.getAddressIdGHN());

        if (facility.getManager() != null) {
            dto.setManagerId(facility.getManager().getStaffID());
            dto.setManagerName(facility.getManager().getFullname());
        }

        dto.setHasInventory(inventoryRepository.existsByFacilityAndQuantityGreaterThan(facility, 0));
        dto.setHasStaff(staffRepository.existsByFacility(facility));
        dto.setHasUnfinishedOrders(orderRepository.existsByFacilityAndStatusNot(facility, "DaGiao"));

        return dto;
    }

}
