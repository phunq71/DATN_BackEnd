package com.main.service;

import com.main.dto.OrderDTO;
import com.main.dto.OrderDetailDTO;
import com.main.dto.OrderItemDTO;
import com.main.dto.OrderPriceDTO;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;

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

    Boolean checkOrderDetailByCustomerIDAndODID(String customerId, Integer orderDetailID);


    ResponseEntity<?> handleReviewAccess(Integer orderDetailID, String customerId);
}
