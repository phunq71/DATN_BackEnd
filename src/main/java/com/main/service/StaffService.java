package com.main.service;

import com.main.dto.DemoteStaffDTO;
import com.main.dto.StaffDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StaffService {
    public Page<StaffDTO> getAllStaffs(int pageNumber, String areaId, String shopId, String keyWord);

    public Page<StaffDTO> getAdmin(int pageNumber, String keyWord);

    public boolean checkAreaOfManager(String areaId, String managerId);

    public boolean checkFacilityOfManager(String facilityId, String managerId);

    public StaffDTO createStaff(StaffDTO staffDTO);

    public StaffDTO updateStaff(StaffDTO staffDTO);

    public boolean demoteStaff(DemoteStaffDTO demoteStaffDTO);

    public boolean deleteStaff(String staffID, boolean isManager);
}
