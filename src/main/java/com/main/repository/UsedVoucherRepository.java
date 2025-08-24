package com.main.repository;

import com.main.entity.Customer;
import com.main.entity.UsedVoucher;
import com.main.entity.UsedVoucherID;
import com.main.entity.Voucher;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsedVoucherRepository extends JpaRepository<UsedVoucher, UsedVoucherID> {

    UsedVoucher findByVoucherAndCustomer(Voucher voucher, Customer customer);

    List<UsedVoucher> getUsedVoucherByVoucher(Voucher voucher);

    boolean existsByVoucher(Voucher voucher);
}
