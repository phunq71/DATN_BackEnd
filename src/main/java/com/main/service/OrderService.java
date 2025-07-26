package com.main.service;

import com.main.dto.*;
import com.main.entity.Order;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface OrderService {
    List<OrderDTO> getOrdersByCustomerIdAndStatus(String customerId, String status, Integer year);

    OrderPriceDTO getOrderPrice(Integer orderId);

    List<OrderPriceDTO> getOrderPricesByCustomer(String customerId, String status, Integer year);

    List<OrderItemDTO> getOrderItemsByOrderId(Integer orderId);

    boolean existsByOrderIDAndCustomer_CustomerId(Integer orderID, String customerCustomerId);

    OrderDetailDTO getOrderDetailByOrderID(Integer orderID);

    List<Integer> getOrderYearByCustomerId(String customerId);

    List<OrderDTO> getOrdersByKeyword(String customerId, String keyword);

    List<OrderDTO> getOrdersByCustomerIdAndOrderID(String customerId, String orderID);

    Map<String, Object> getOrderPreviewData( List<OrderPreviewDTO> items );

    Boolean checkOrderDetailByCustomerIDAndODID(String customerId, Integer orderDetailID);


    ResponseEntity<?> handleReviewAccess(Integer orderDetailID, String customerId);

    Map<String, Object> getOrderStatus(String orderCode);

    Order findOrderByID(Integer orderId);

    Boolean saveOrders(List<OrderDTO> orders);

    Boolean addOrderCustomer(Map<String, Object> checkoutInfo);

}
