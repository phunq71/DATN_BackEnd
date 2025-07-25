package com.main.service;

import com.main.dto.ReturnRequestDTO;

import java.util.List;

public interface ReturnRequestService {
    List<ReturnRequestDTO> getReturnRequestByCustomerID(String customerID, int year);

    ReturnRequestDTO getReturnRequestByID(int returnRequestID, String customerID);
}
