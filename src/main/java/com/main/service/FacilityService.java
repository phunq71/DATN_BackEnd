package com.main.service;

import com.main.dto.FacilityOrderDTO;
import com.main.dto.OrderPreviewDTO;
import com.main.dto.ProductViewDTO;
import com.main.entity.Facility;

import java.util.List;

public interface FacilityService {

    // dùng cho orders
    public List<FacilityOrderDTO> getAllFacilities(List<OrderPreviewDTO> Items);
}
