package com.main.service;

import com.main.dto.VoucherOrderDTO;
import com.main.entity.UsedVoucher;

import java.math.BigDecimal;
import java.util.List;

public interface UsedVoucherService {
    public List<UsedVoucher> getByVoucherId(String voucherId);
}
