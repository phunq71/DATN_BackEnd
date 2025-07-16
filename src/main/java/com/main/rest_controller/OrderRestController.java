package com.main.rest_controller;

import com.main.dto.OrderDTO;
import com.main.dto.OrderDetailDTO;
import com.main.dto.OrderItemDTO;
import com.main.dto.OrderPriceDTO;
import com.main.service.OrderService;
import com.main.utils.AuthUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class OrderRestController {
    public final OrderService orderService;

    public OrderRestController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/opulentia_user/orders/allOrders/{status}")
    public ResponseEntity<List<OrderDTO>> allOrder(@PathVariable String status) {
        String accountId = AuthUtil.getAccountID();

        if(accountId == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        List<OrderDTO> orderDTOs = orderService.getOrdersByCustomerIdAndStatus(accountId, status);
        return ResponseEntity.ok(orderDTOs);
    }


    @GetMapping("/opulentia_user/orders/{orderId}")
    public ResponseEntity<OrderDetailDTO> getOrder(@PathVariable Integer orderId) {
        String accountId = AuthUtil.getAccountID();

        if(accountId == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if(!orderService.existsByOrderIDAndCustomer_CustomerId(orderId, accountId)){
            System.err.println("Tài khoản " + accountId+ " không được có quyền xem orderId " + orderId);
            return ResponseEntity.badRequest().build();
        }
        OrderDetailDTO orderDetailDTO = orderService.getOrderDetailByOrderID(orderId);

        return ResponseEntity.ok(orderDetailDTO);
    }
}
