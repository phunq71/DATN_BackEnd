package com.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FacilityOrdManagerDTO {
    private String facilityId;
    private String facilityName;
    private String parentId;
    private String parentName;
    // Dùng để xác định phân cấp
}
