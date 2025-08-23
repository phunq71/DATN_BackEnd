package com.main.dto;


import com.main.entity.Staff;
import lombok.Data;

import com.main.utils.AddressUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.regex.Pattern;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StaffDTO {
    private String staffID;
    private String fullName;

    private String accountId; // chỉ lấy ID từ Account

    private String email;
    private String password;
    private String phone;
    private LocalDate dob;
    private String fullAddress;
    private String role;
    private Boolean status;
    private LocalDate createAt;
    private LocalDate updateAt;
    private String facilityID;

    private String province;
    private String district;
    private String ward;
    private String addressDetail;

    public StaffDTO(String staffID, String fullName, String email, String password, String phone, LocalDate dob, String fullAddress, String role, Boolean status, LocalDate createAt, LocalDate updateAt, String facilityID) {
        this.staffID = staffID;
        this.fullName = fullName;
        this.email = email;
        this.password = "";
        this.phone = phone;
        this.dob = dob;
        this.fullAddress = fullAddress;
        this.role = role;
        this.status = status;
        this.createAt = createAt;
        this.updateAt = updateAt;
        this.facilityID = facilityID;

        String[] address= AddressUtil.splitAddress(fullAddress);
        this.addressDetail = address[0];
        this.ward = address[1];
        this.district = address[2];
        this.province = address[3];
    }


    public StaffDTO (String staffID, String fullName, String accountId) {
        this.staffID = staffID;
        this.fullName = fullName;
        this.accountId = accountId;
    }

}

