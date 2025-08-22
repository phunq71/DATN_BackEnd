package com.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
public class CustomerManagementDTO {
    private String customerId;
    private String fullName;
    private String phone;
    private Boolean gender;
    private String address;
    private String addressIdGHN;
    private LocalDate dob;
    private String imageAvt;
    private String membershipId;
    private String membership;
    private LocalDate createAt;
    private LocalDate updateAt;
    private Boolean status;
    List<CusManagement_orderDTO> orders;
    public CustomerManagementDTO(
            String customerId,
            String fullName,
            String phone,
            Boolean gender,
            String address,
            String addressIdGHN,
            LocalDate dob,
            String imageAvt,
            String membershipId,
            String membership,
            LocalDate createAt,
            LocalDate updateAt,
            Boolean status
    ) {
        this.customerId = customerId;
        this.fullName = fullName;
        this.phone = phone;
        this.gender = gender;
        this.address = address;
        this.addressIdGHN = addressIdGHN;
        this.dob = dob;
        this.imageAvt = imageAvt;
        this.membershipId = membershipId;
        this.membership = membership;
        this.createAt = createAt;
        this.updateAt = updateAt;
        this.status = status;
    }

}

