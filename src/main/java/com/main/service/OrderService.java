package com.main.service;

import com.main.dto.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderService {
    List<OrderDTO> getOrdersByCustomerIdAndStatus(String customerId, String status, Integer year);

    OrderPriceDTO getOrderPrice(Integer orderId);

    List<OrderPriceDTO> getOrderPricesByCustomer(String customerId, String status, Integer year);

    List<OrderItemDTO> getOrderItemsByOrderId(Integer orderId);

    boolean existsByOrderIDAndCustomer_CustomerId(Integer orderID, String customerCustomerId);

    OrderDetailDTO getOrderDetailByOrderID(Integer orderID);

    List<Integer> getOrderYearByCustomerId(String customerId);

    List<OrderDTO> getOrdersByKeyword(String customerId, String keyword);

    List<OrderPreviewDTO> getOrderPreviewProducts();
}
