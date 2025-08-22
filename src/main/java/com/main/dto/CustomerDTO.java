package com.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class CustomerDTO {
    private String customerId;
    private String fullName;
    private Boolean gender;
    private String address;
    private String rank;
    private LocalDate dob;
    private String imageAvt;
    private String addressDetail;
    private String ward;
    private String district;
    private String province;
    private Boolean isOAuth2;

    private String addressIdGHN;

    public CustomerDTO(String customerId, String fullName, Boolean gender, String address, String rank, LocalDate dob, String imageAvt, String addressIdGHN) {
        this.customerId = customerId;
        this.fullName = fullName;
        this.gender = gender;
        this.address = address;
        this.rank = rank;
        this.dob = dob;
        this.imageAvt = imageAvt;
        this.addressIdGHN = addressIdGHN;
    }

    // Constructor đầy đủ (có rank)
    public CustomerDTO(String customerId, String fullName, Boolean gender, String address,
                       LocalDate dob, String imageAvt,
                       String addressDetail, String ward, String district, String province,
                       Boolean isOAuth2, String addressIdGHN) {
        this.customerId = customerId;
        this.fullName = fullName;
        this.gender = gender;
        this.address = address;
        this.dob = dob;
        this.imageAvt = imageAvt;
        this.addressDetail = addressDetail;
        this.ward = ward;
        this.district = district;
        this.province = province;
        this.isOAuth2 = isOAuth2;
        this.addressIdGHN = addressIdGHN;
    }
}
