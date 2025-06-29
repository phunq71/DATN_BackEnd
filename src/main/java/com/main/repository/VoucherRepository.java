package com.main.repository;

import com.main.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, String> {

}
