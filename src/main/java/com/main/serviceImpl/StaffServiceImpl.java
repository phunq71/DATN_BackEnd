package com.main.serviceImpl;

import com.main.entity.Staff;
import com.main.repository.StaffRepository;
import com.main.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StaffServiceImpl implements StaffService {
    @Autowired
    StaffRepository staffRepository;
    @Override
    public Staff getStaffByID(String id) {
        return staffRepository.getByStaffID(id);
    }

    @Override
    public Staff getStaffByAccountID(String id) {
        return staffRepository.getByStaffID(id);
    }
}
