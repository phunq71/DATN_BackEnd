package com.main.serviceImpl;
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
}
