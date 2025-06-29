package com.main.repository;

import com.main.entity.UsedVoucher;
import com.main.entity.UsedVoucherID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsedVoucherRepository extends JpaRepository<UsedVoucher, UsedVoucherID> {
}
