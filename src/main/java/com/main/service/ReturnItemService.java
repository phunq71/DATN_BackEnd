package com.main.service;

import com.main.dto.ReturnItemDTO;
import com.main.dto.ReturnItemFormDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReturnItemService {
    public List<ReturnItemDTO> getReturnItemByOrderID(int orderID);

    public boolean checkOrderAndCustomer(int orderID, String CustomerID);

    public boolean saveReturnItem(int orderID, List<ReturnItemFormDTO> items, String CustomerID, List<MultipartFile> savedFiles);
}
