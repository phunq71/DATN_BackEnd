package com.main.dto;

import com.main.enums.OrderStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderDetailDTO {
    private Integer orderID;
    private LocalDateTime orderDate;
    private String status;
    private String statusName;
    private List<OrderItemDTO> items;

    private BigDecimal finalPrice;
    private BigDecimal discountVoucherPrice;
    private BigDecimal discountProductPrice;

    private LocalDateTime updateStatusAt;
    private String shippingAddress;
    private String shippingName;
    private String shippingPhone;
    private BigDecimal shippingCost;
    private BigDecimal shippinngCostDiscountPrice;
    private String paymentMethod;
    private LocalDateTime transactionDate;
    private String shippingCode;
    private String shippingStatus;
    private String updatedTime;
    private List<ShippingLogDTO> trackingHistory;


    public OrderDetailDTO(Integer orderID, LocalDateTime orderDate, String status, String address) {
        this.orderID = orderID;
        this.orderDate = orderDate;
        this.status = status;
        OrderStatusEnum statusEnum = OrderStatusEnum.fromDbValue(status);
        this.statusName = statusEnum.getDisplayName();
        this.shippingAddress= address;
    }


    public OrderDetailDTO(Integer orderID, LocalDateTime orderDate, String status, String shippingAddress, String shippingName, String shippingPhone, BigDecimal shippingCost,BigDecimal shippinngCostDiscountPrice , String paymentMethod, LocalDateTime transactionDate, LocalDateTime updateStatusAt, String shippingCode) {
        this.orderID = orderID;
        this.orderDate = orderDate;
        this.status = status;
        OrderStatusEnum statusEnum = OrderStatusEnum.fromDbValue(status);
        this.statusName = statusEnum.getDisplayName();
        this.shippingAddress = shippingAddress;
        this.shippingName = shippingName;
        this.shippingPhone = shippingPhone;
        this.shippingCost = shippingCost;
        this.shippinngCostDiscountPrice = shippinngCostDiscountPrice;
        this.paymentMethod = paymentMethod;
        this.transactionDate = transactionDate;
        this.updateStatusAt = updateStatusAt;
        this.shippingCode = shippingCode;
    }
}
