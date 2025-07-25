package com.main.serviceImpl;

import com.main.dto.ReturnItemDTO;
import com.main.dto.ReturnRequestDTO;
import com.main.repository.ReturnRequestRepository;
import com.main.service.ReturnRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReturnRequestServiceImpl implements ReturnRequestService {
    private final ReturnRequestRepository returnRequestRepository;

    @Override
    public List<ReturnRequestDTO> getReturnRequestByCustomerID(String customerID, int year) {
        List<ReturnRequestDTO> returnRequests = returnRequestRepository.getReturnRequestByCustomerID(customerID, year);
        returnRequests.forEach(returnRequest -> {
           List<ReturnItemDTO> returnItemDTOs= returnRequestRepository.getReturnItemsByReturnRequestID(returnRequest.getReturnRequestId());
           returnRequest.setItems(returnItemDTOs);
           returnRequest.setTotalPrice();
        });
        return returnRequests;
    }

    @Override
    public ReturnRequestDTO getReturnRequestByID(int returnRequestID, String customerID) {
        ReturnRequestDTO returnRequest = returnRequestRepository.getReturnRequestByID(returnRequestID, customerID);
        if (returnRequest == null) { return null;}
        List<ReturnItemDTO> returnItemDTOs = returnRequestRepository.getReturnItemsByReturnRequestID(returnRequestID);
        returnItemDTOs.forEach(returnItemDTO -> {
            returnItemDTO.setEvidenceImages(returnRequestRepository.getImagesByReturnItemsId(returnItemDTO.getReturnItemID()));
        });
        returnRequest.setItems(returnItemDTOs);
        returnRequest.setTotalPrice();
        return returnRequest;
    }


}
