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
    private LocalDate dob;
    private String imageAvt;

    private String addressDetail;
    private String ward;
    private String district;
    private String province;
    private Boolean isOAuth2;

    public CustomerDTO(String customerId, String fullName, Boolean gender, String address, LocalDate dob, String imageAvt) {
        this.customerId = customerId;
        this.fullName = fullName;
        this.gender = gender;
        this.address = address;
        this.dob = dob;
        this.imageAvt = imageAvt;
    }
}
