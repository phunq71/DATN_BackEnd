package com.main.serviceImpl;

import com.main.entity.UsedVoucher;
import com.main.entity.Voucher;
import com.main.repository.UsedVoucherRepository;
import com.main.repository.VoucherRepository;
import com.main.service.UsedVoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsedVoucherServiceImpl implements UsedVoucherService {
    private final UsedVoucherRepository usedVoucherRepository;
    private final VoucherRepository voucherRepository;
    @Override
    public List<UsedVoucher> getByVoucherId(String voucherId) {
        Voucher voucher = voucherRepository.findById(voucherId).get();
        return usedVoucherRepository.getUsedVoucherByVoucher(voucher);
    }
}
