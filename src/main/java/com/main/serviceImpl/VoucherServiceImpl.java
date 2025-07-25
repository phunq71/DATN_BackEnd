package com.main.serviceImpl;

import com.main.dto.VoucherOrderDTO;
import com.main.entity.Customer;
import com.main.entity.Voucher;
import com.main.mapper.CustomerMapper;
import com.main.mapper.VoucherMapper;
import com.main.repository.CustomerRepository;
import com.main.repository.VoucherRepository;
import com.main.service.VoucherService;
import com.main.utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VoucherServiceImpl implements VoucherService {

    private final VoucherRepository voucherRepository;
    private final VoucherMapper voucherMapper;
    private final CustomerRepository customerRepository;


    @Override
    public List<VoucherOrderDTO> getListVoucherFromOrder(BigDecimal totalAmount) {
        Customer customer = new Customer();
        customer = customerRepository.findByCustomerId(AuthUtil.getAccountID());

        List<Voucher> voucherList = new ArrayList<>();
        voucherList = voucherRepository.findValidVouchersForCustomer(customer.getMembership().getMembershipId(), customer.getCustomerId());
        List<Voucher> unclaimedList = voucherRepository.findUnclaimedVouchersByCustomer(customer.getCustomerId());

        voucherList.addAll(unclaimedList);

        List<VoucherOrderDTO> voucherOrderDTOList = new ArrayList<>();
        voucherList.forEach(voucher -> {
            VoucherOrderDTO voucherOrderDTO = new VoucherOrderDTO();
            voucherOrderDTO = voucherMapper.voucherToVoucherOrderDTONoIsUse(voucher);
            if (totalAmount.compareTo(voucherOrderDTO.getMinOrderValue()) >= 0){
                voucherOrderDTO.setIsUse(true);
            }else{
                voucherOrderDTO.setIsUse(false);
            }
            voucherOrderDTOList.add(voucherOrderDTO);
        });

        voucherOrderDTOList.sort(Comparator
                .comparing(VoucherOrderDTO::getIsUse).reversed()
                .thenComparing(VoucherOrderDTO::getDiscountValue, Comparator.reverseOrder()));

        return voucherOrderDTOList;
    }


    @Override
    public VoucherOrderDTO getNearestVoucherToReach(String membershipId, BigDecimal totalAmount) {
        Pageable topOne = PageRequest.of(0, 1);
        List<Voucher> vouchers = voucherRepository.findNearestVoucherByMembershipJPQL(membershipId, totalAmount, AuthUtil.getAccountID());
        // Sắp xếp tăng dần theo khoảng cách cần đạt được
        vouchers.sort(Comparator.comparing(v -> v.getClaimConditions().subtract(totalAmount)));
        return vouchers.isEmpty() ? null : voucherMapper.voucherToVoucherOrderDTONoIsUse(vouchers.get(0));
    }

    @Override
    public VoucherOrderDTO getBestMatchedVoucher(String membershipId, BigDecimal totalAmount) {
        Pageable topOne = PageRequest.of(0, 1);
        List<Voucher> vouchers = voucherRepository.findBestMatchedVoucher(membershipId, totalAmount,AuthUtil.getAccountID() );
        vouchers.sort(Comparator.comparing(v -> totalAmount.subtract(v.getClaimConditions())));
        return vouchers.isEmpty() ? null : voucherMapper.voucherToVoucherOrderDTONoIsUse(vouchers.get(0));
    }
}
