package com.main.mapper;

import com.main.dto.Order_ReviewDTO;
import com.main.entity.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {
    public static Order_ReviewDTO toDTO(Order order) {
        if (order == null) return null;

        Order_ReviewDTO dto = new Order_ReviewDTO();
        dto.setOrderID(order.getOrderID());
        dto.setOrderDate(order.getOrderDate());
        dto.setStatus(order.getStatus());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setNote(order.getNote());
        dto.setIsOnline(order.getIsOnline());
        dto.setShipMethod(order.getShipMethod());
        dto.setCostShip(order.getCostShip());
        dto.setCustomerID(order.getCustomer().getCustomerId());
        dto.setStaffID(order.getStaff().getStaffID());
        dto.setVoucherID(order.getVoucher().getVoucherID());
        dto.setFacilityID(order.getFacility().getFacilityId());
        return dto;
    }
}
