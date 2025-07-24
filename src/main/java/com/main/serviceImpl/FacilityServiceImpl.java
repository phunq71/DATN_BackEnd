package com.main.serviceImpl;
import com.main.dto.FacilityOrderDTO;
import com.main.dto.OrderPreviewDTO;
import com.main.entity.Facility;
import com.main.entity.Inventory;
import com.main.entity.InventoryId;
import com.main.repository.FacilityRepository;
import com.main.repository.InventoryRepository;
import com.main.repository.OrderRepository;
import com.main.service.FacilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FacilityServiceImpl implements FacilityService {

    private final FacilityRepository facilityRepository;

    private final OrderRepository orderRepository;

    private final InventoryRepository inventoryRepository;


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

}
