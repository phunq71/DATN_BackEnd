package com.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DemoteStaffDTO {
    private String areaId;
    private String shopId;
    private String newRole;
}
