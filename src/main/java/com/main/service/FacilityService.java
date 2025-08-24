package com.main.service;

import com.main.dto.FacilityDTO;
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

    // Lấy toàn bộ cơ sở/khu vực (có thể là dạng cây)
    List<FacilityDTO> getAllFacilities();

    // Lấy chi tiết
    FacilityDTO getFacilityById(String facilityId);

    FacilityDTO createFacility(FacilityDTO dto);

    FacilityDTO updateFacility(String facilityId, FacilityDTO dto);

    // Di chuyển cơ sở sang khu vực khác
    void moveFacility(String facilityId, String toParentId);

    // Xóa hoặc tắt hoạt động cơ sở
    FacilityDTO deleteOrDeactivateFacility(String facilityId);
}
