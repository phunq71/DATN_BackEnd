package com.main.service;

import com.main.dto.FacilityOrdManagerDTO;
import com.main.dto.FacilityOrderDTO;
import com.main.dto.OrderPreviewDTO;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FacilityService {
    // dùng cho orders
    public List<FacilityOrderDTO> getAllFacilities(List<OrderPreviewDTO> Items);

    public List<FacilityOrdManagerDTO> getShop();

    public List<FacilityOrdManagerDTO> getShopByManager_ID(String managerID);

    List<FacilityOrdManagerDTO> getShopByStaffID(String staffId);
}
