package com.main.dto;

import com.main.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerRegisterDTO {
    private String email;
    private String password;
    private String confirmPassword;
    private String fullname;
    private Boolean gender;
    private String address;
    private String fullAddressID;
    private LocalDate dob;
    private String image;
}