package com.main.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrdManagement_OrderDTO {
    private Integer orderID;
    private LocalDateTime orderDate;
    private String status;
    private LocalDateTime updatedStatusAt;
    private String shippingAddress;
    private String note;
    private Boolean isOnline;
    private String shipMethod;
    private String addressIdGHN;
    private String customerName;

    private String staffName;

    private String facilityName;

    private LocalDateTime transactionDate;
    private BigDecimal totalAmount;
    private List<OrdManagement_ProductDTO> products;

    public OrdManagement_OrderDTO(Integer orderID, LocalDateTime orderDate
            , String status, LocalDateTime updatedStatusAt, String shippingAddress
            , String note, Boolean isOnline, String shipMethod, String addressIdGHN
            , String customerName, String staffName, String facilityName
            , LocalDateTime transactionDate, BigDecimal totalAmount) {
        this.orderID = orderID;
        this.orderDate = orderDate;
        this.status = status;
        this.updatedStatusAt = updatedStatusAt;
        this.shippingAddress = shippingAddress;
        this.note = note;
        this.isOnline = isOnline;
        this.shipMethod = shipMethod;
        this.addressIdGHN = addressIdGHN;
        this.customerName = customerName;
        this.staffName = staffName;
        this.facilityName = facilityName;
        this.transactionDate = transactionDate;
        this.totalAmount = totalAmount;
    }
}
