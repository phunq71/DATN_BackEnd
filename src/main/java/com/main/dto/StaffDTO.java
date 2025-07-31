package com.main.dto;

import lombok.Data;

@Data
public class StaffDTO {
    private String staffID;
    private String fullName;
    private String accountId; // chỉ lấy ID từ Account
}