package com.main.service;

import com.main.dto.InvSlipDTO;
import com.main.dto.InvSlipDetailDTO;
import com.main.dto.ItemInvSlipDTO;
import com.main.entity.InventorySlip;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface InventorySlipService {
    public Page<InvSlipDTO> getInventorySlipDTO(int pageN, LocalDate date, List<String> facilityIds, Boolean type);

    public Page<ItemInvSlipDTO> getItemInvSlipDtos(int pageN, String keyword, String facilityId);

    public List<InvSlipDetailDTO> checkQuantityLimit(String facilityId, List<InvSlipDetailDTO> details);

    public boolean checkQuantityInventories(String facilityId, List<InvSlipDetailDTO> details);

    public InventorySlip createInventorySlip(InvSlipDTO invSlipDTO);

    public Long getBageCount(String managerId, String facilityId);

    public List<InvSlipDTO> getPendingSlips(String managerId, String facilityId);

    public InventorySlip getInvSlipById(String id);

    public void approveInvSlip(InventorySlip inventorySlip, boolean isApproved);

    public void updateDoneExportSlip(String id);

    public void sendMail(InventorySlip inventorySlip);
}
