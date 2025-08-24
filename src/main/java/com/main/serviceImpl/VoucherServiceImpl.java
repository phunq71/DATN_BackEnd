package com.main.serviceImpl;

import com.main.dto.VoucherManagermetDTO;
import com.main.dto.VoucherOrderDTO;
import com.main.entity.*;
import com.main.mapper.CustomerMapper;
import com.main.mapper.VoucherMapper;
import com.main.repository.*;
import com.main.service.MailService;
import com.main.service.UsedVoucherService;
import com.main.service.VoucherService;
import com.main.utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VoucherServiceImpl implements VoucherService {

    private final VoucherRepository voucherRepository;
    private final VoucherMapper voucherMapper;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final TransactionRepository transactionRepository;
    private final UsedVoucherRepository usedVoucherRepository;
    private final MailService mailService;


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

    @Override
    public void addVoucherCustomerOrderSuccess(Integer orderId) {
        Order order = new Order();
        order = orderRepository.findByOrderID(orderId);
        Customer customer = new Customer();
        customer = customerRepository.findByCustomerId(order.getCustomer().getCustomerId());
        BigDecimal totalAmount = new BigDecimal(0);
        totalAmount = transactionRepository.findByOrder_OrderID(orderId).getAmount();

        VoucherOrderDTO voucher = getNearestVoucherToReach(customer.getMembership().getMembershipId(), totalAmount);

        UsedVoucher usedVoucher = new UsedVoucher();
        usedVoucher.setCustomer(customer);
        usedVoucher.setType(false);
        usedVoucher.setVoucher( voucherRepository.findById(voucher.getVoucherID()).get());
        UsedVoucherID usedVoucherID = new UsedVoucherID();
        usedVoucherID.setCustomer(order.getCustomer().getCustomerId());
        usedVoucherID.setVoucher(voucher.getVoucherID());
        usedVoucher.setUsedVoucherID(usedVoucherID);

        usedVoucherRepository.save(usedVoucher);



        mailService.sendVoucherEmail( order.getCustomer().getAccount().getEmail()
                    , voucher.getVoucherID()
                    , voucher.getDiscountValue()
                    , voucher.getEndDate());


    }

    @Override
    public List<VoucherManagermetDTO> findVoucherByPromotionId(String promotionId) {
        return voucherRepository.findVoucherByPromotionId(promotionId);
    }

    @Override
    public String findMaxVoucherId() {
        Voucher voucher = voucherRepository.findTop1ByOrderByVoucherIDDesc();
        return voucher != null ? voucher.getVoucherID() : null;
    }

    @Override
    public Voucher save(Voucher voucher) {
        return voucherRepository.save(voucher);
    }

    @Override
    public Voucher findVoucherById(String voucherId) {
        return voucherRepository.findById(voucherId).get();
    }

    @Override
    @Transactional
    public Boolean deleteVoucher(String voucherId) {
        // Kiểm tra voucher có tồn tại không
        Optional<Voucher> optionalVoucher = voucherRepository.findById(voucherId);
        if (optionalVoucher.isEmpty()) {
            return false;
        }
        Voucher voucher = optionalVoucher.get();
        System.err.println("ừng ở voucher: "+voucher.getVoucherID());
        // Nếu đã có bản ghi dùng voucher thì không cho xóa
        if (usedVoucherRepository.existsByVoucher(voucher)){
            System.out.println("Có ngời dun rồi nh, dung co xoa");
            return false;
        }
        System.err.println("ừng ở usedVoucherRepository: ");
        // Xóa voucher
        voucherRepository.deleteVoucher(voucherId);
        System.err.println("ừng khi delete ");
        return true;
    }

}
