package com.main.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Setter
@Getter
public class Order_ReviewDTO {
    private Integer orderID;
    private LocalDateTime orderDate;
    private String status;
    private String shippingAddress;
    private String note;
    private Boolean isOnline;
    private String shipMethod;
    private BigDecimal costShip;
    private String customerID;
    private String staffID;
    private String voucherID;
    private String facilityID;
}
