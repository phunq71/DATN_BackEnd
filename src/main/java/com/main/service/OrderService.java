package com.main.service;

import com.main.dto.*;
import com.main.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
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

    public Page<OrdManagement_OrderDTO> getOrders(LocalDateTime startDate,LocalDateTime endDate
            ,String status, String facilityId,String parentId,Integer orderId, Pageable pageable);
    public Page<OrdManagement_OrderDTO> getOrdersWithOrderDate(Pageable pageable
                ,LocalDateTime orderDate
                , String status);
    public List<OrdManagement_ProductDTO> getProductsByOrderID(Integer orderID);
    public Order save(Order order);
    Boolean addOrderCustomer(Map<String, Object> checkoutInfo);
    public List<OrderDetailDTO> getAllOrderIdShippingCodes();
    public OrderDetailDTO getOrderIdByShippingCodes(String shippingCode);
    String createGhnOrder(GhnOrderRequestDTO payload);
}
