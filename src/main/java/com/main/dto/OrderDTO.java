package com.main.dto;

import com.main.enums.OrderStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class OrderDTO {
    private Integer orderID;
    private LocalDateTime orderDate;
    private String status;
    private String statusName;
    private List<OrderItemDTO> items;
    private BigDecimal totalPrice;
    private BigDecimal shippingCosts;
    private String shippingCode;


    public OrderDTO(Integer orderID, LocalDateTime orderDate, String status, BigDecimal shippingCosts, String shippingCode) {
        this.orderID = orderID;
        this.orderDate = orderDate;
        this.status = status;
        OrderStatusEnum statusEnum = OrderStatusEnum.fromDbValue(status);
        this.statusName = statusEnum.getDisplayName();
        this.shippingCosts= shippingCosts;
        this.shippingCode = shippingCode;
    }
    public OrderDTO(Integer orderID, LocalDateTime orderDate, String status, BigDecimal shippingCosts) {
        this.orderID = orderID;
        this.orderDate = orderDate;
        this.status = status;
        OrderStatusEnum statusEnum = OrderStatusEnum.fromDbValue(status);
        this.statusName = statusEnum.getDisplayName();
        this.shippingCosts= shippingCosts;
    }
}
