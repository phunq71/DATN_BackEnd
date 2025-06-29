package com.main.entity;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructors
public class UsedVoucherID implements Serializable {
    private Voucher voucher;
    private Customer customer;
}
