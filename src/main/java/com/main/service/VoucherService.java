package com.main.service;

import com.main.dto.VoucherManagermetDTO;
import com.main.dto.VoucherOrderDTO;
import com.main.entity.Voucher;

import java.math.BigDecimal;
import java.util.List;

public interface VoucherService {

    public List<VoucherOrderDTO> getListVoucherFromOrder(BigDecimal totalAmount);
    VoucherOrderDTO getNearestVoucherToReach(String membershipId, BigDecimal totalAmount);
    VoucherOrderDTO getBestMatchedVoucher(String membershipId, BigDecimal totalAmount);
    public void addVoucherCustomerOrderSuccess(Integer orderId);
    List<VoucherManagermetDTO> findVoucherByPromotionId(String promotionId);
    String findMaxVoucherId();
    Voucher save(Voucher voucher);
    Voucher findVoucherById(String voucherId);
    Boolean deleteVoucher(String voucherId);
}
