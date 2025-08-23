package com.main.mapper;

import com.main.dto.FacilityOrdManagerDTO;
import com.main.entity.Facility;
import org.springframework.stereotype.Component;

@Component
public class FacilityMapper {
//    public static FacilityOrdManagerDTO toDTO(Facility facility) {
//        FacilityOrdManagerDTO dto = new FacilityOrdManagerDTO("","");
//        dto.setFacilityId(facility.getFacilityId());
//        dto.setFacilityName(facility.getFacilityName());
//        // ⚠️ Kiểm tra parent null
//        if (facility.getParent() != null) {
//            dto.setParentId(facility.getParent().getFacilityId());
//        }
//        // ⚠️ Kiểm tra manager null
//        if (facility.getManager() != null) {
//            dto.setManagerId(facility.getManager().getStaffID());
//        }
//        if (facility.getManager() != null) {
//            dto.setManager(StaffMapper.toDTO(facility.getManager())); // dùng DTO
//        }
//        dto.setType(facility.getType());
//        return dto;
//    }
//}
}
