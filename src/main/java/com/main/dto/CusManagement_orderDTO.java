package com.main.dto;

import com.main.entity.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CusManagement_orderDTO {
    private Integer orderID;
    private LocalDateTime orderDate;
    private String status;
    private String shippingAddress;
    private String shippingCode;
    private Boolean isOnline;
    private String staffId;
    private String voucherId;
    private String facilityId;
    private BigDecimal amount;
//    private List<CusManagement_productDTO> products;
}
