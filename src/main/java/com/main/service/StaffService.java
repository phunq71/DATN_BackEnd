package com.main.service;

import com.main.entity.Staff;

public interface StaffService {
    public Staff getStaffByID(String id);

    public Staff getStaffByAccountID(String id);
}
