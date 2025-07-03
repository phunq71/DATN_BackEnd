package com.main.mapper;

import com.main.dto.OrderDetail_ReviewDTO;
import com.main.entity.OrderDetail;
import org.springframework.stereotype.Component;

@Component
public class OrderDetailMapper {
    public static OrderDetail_ReviewDTO toDTO(OrderDetail detail) {
        if (detail == null) return null;

        OrderDetail_ReviewDTO dto = new OrderDetail_ReviewDTO();
        dto.setOrderDetailID(detail.getOrderDetailID());
        dto.setOrderID(detail.getOrder().getOrderID());
        dto.setItemID(detail.getItem().getItemId());
        dto.setUnitPrice(detail.getUnitPrice());
        dto.setQuantity(detail.getQuantity());
        dto.setTotalPrice(detail.getTotalPrice());
        return dto;
    }
}
