package com.main.mapper;

import com.main.dto.VoucherOrderDTO;
import com.main.entity.Voucher;
import org.springframework.stereotype.Component;

@Component
public class VoucherMapper {

    public VoucherOrderDTO voucherToVoucherOrderDTONoIsUse(Voucher voucher) {
        VoucherOrderDTO voucherOrderDTO = new VoucherOrderDTO();
        voucherOrderDTO.setVoucherID(voucher.getVoucherID());
        voucherOrderDTO.setDiscountType(voucher.getDiscountType());
        voucherOrderDTO.setDiscountValue(voucher.getDiscountValue());
        voucherOrderDTO.setEndDate(voucher.getEndDate());
        voucherOrderDTO.setMinOrderValue(voucher.getMinOrderValue());
        voucherOrderDTO.setType(voucher.getType());
        voucherOrderDTO.setClaimConditions(voucher.getClaimConditions());
        return voucherOrderDTO;
    }

}
