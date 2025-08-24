package com.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacilityDTO {
    private String facilityId;
    private String type;
    private String facilityName;
    private String address;
    private Boolean isUse;
    private String parentId;
    private String addressIdGHN;

    // Thông tin quản lý
    private String managerId;
    private String managerName;

    // Thông tin kiểm tra điều kiện xóa / tắt
    private boolean hasInventory;
    private boolean hasStaff;
    private boolean hasUnfinishedOrders;
    private String deleteMessage;
    // Cấu trúc cây
    private List<FacilityDTO> children = new ArrayList<>();
}
