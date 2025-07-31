package com.main.mapper;

import com.main.dto.StaffDTO;
import com.main.entity.Staff;
import org.springframework.stereotype.Component;

@Component
public class StaffMapper {
    public static StaffDTO toDTO(Staff staff) {
        if (staff == null) return null;

        StaffDTO dto = new StaffDTO();
        dto.setStaffID(staff.getStaffID());
        dto.setFullName(staff.getFullname());
        dto.setAccountId(staff.getAccount() != null ? staff.getAccount().getAccountId() : null);
        return dto;
    }
}

