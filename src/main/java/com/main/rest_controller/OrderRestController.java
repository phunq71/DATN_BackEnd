package com.main.rest_controller;

import com.main.dto.*;
import com.main.entity.Order;
import com.main.entity.Transaction;
import com.main.enums.OrderStatusEnum;
import com.main.repository.TransactionRepository;
import com.main.service.OrderService;
import com.main.service.TransactionService;
import com.main.service.VoucherService;
import com.main.utils.AuthUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
public class OrderRestController {
    public final OrderService orderService;
    private final TransactionRepository transactionRepository;
    private final TransactionService transactionService;
    private final VoucherService voucherService;

    public OrderRestController(OrderService orderService, TransactionRepository transactionRepository, TransactionService transactionService, VoucherService voucherService) {
        this.orderService = orderService;
        this.transactionRepository = transactionRepository;
        this.transactionService = transactionService;
        this.voucherService = voucherService;
    }

//    @GetMapping("/opulentia_user/orders/allOrders/{status}/{year}")
//    public ResponseEntity<List<OrderDTO>> allOrder1(@PathVariable String status, @PathVariable Integer year) {
//        String accountId = AuthUtil.getAccountID();
//
//        if(accountId == null){
//            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//        }
//
//        List<OrderDTO> orderDTOs = orderService.getOrdersByCustomerIdAndStatus(accountId, status, year);
//        List<OrderDTO> saveOrderIds = new ArrayList<>();
//        if(orderDTOs.isEmpty()){
//            return ResponseEntity.ok(new ArrayList<>());
//        }
//        for(OrderDTO orderDTO : orderDTOs){
//            if(orderDTO.getStatus().equals("SanSangGiao")||orderDTO.getStatus().equals("ChoGiaoHang")) {
//                Map<String, Object> mapOrderGHN = orderService.getOrderStatus(orderDTO.getShippingCode());
//
//                String newStatus =  mapGHNStatusToSystem(mapOrderGHN.get("shippingStatus").toString());
//                if(newStatus!=null){
//                   orderDTO.setStatus(newStatus);
//               }
//                System.err.println("Orrder staatuss: " + orderDTO.getStatus());
//                saveOrderIds.add(orderDTO);
//            }
//        }
//        orderService.saveOrders(saveOrderIds);
//
//        return ResponseEntity.ok(orderDTOs);
//    }
    @GetMapping("/opulentia_user/orders/allOrders/{status}/{year}")
    public ResponseEntity<List<OrderDTO>> allOrder(@PathVariable String status, @PathVariable Integer year) {
        String accountId = AuthUtil.getAccountID();

        if (accountId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        List<OrderDTO> orderDTOs = orderService.getOrdersByCustomerIdAndStatus(accountId, status, year);
//        for (OrderDTO orderDTO : orderDTOs) {
//            System.out.println(orderDTO.getTotalPrice() + "Test");
//        }
//        System.err.println("OOOOOOOOOO"+orderDTOs.size());
        List<OrderDTO> saveOrderIds = new ArrayList<>();

        if (orderDTOs.isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());
        }

        for (OrderDTO orderDTO : orderDTOs) {
            String currentStatus = orderDTO.getStatus();
            System.out.println("😚😚😚😚😚😚😚😚");
            if ("SanSangGiao".equals(currentStatus) || "ChoGiaoHang".equals(currentStatus)) {
                System.err.println("🔍 Kiểm tra đơn hàng: " + orderDTO.getOrderID() + ", shippingCode: " + orderDTO.getShippingCode());

                Map<String, Object> mapOrderGHN = orderService.getOrderStatus(orderDTO.getShippingCode());
                Object ghnStatusObj = mapOrderGHN.get("shippingStatus");

                if (ghnStatusObj != null) {
                    String ghnStatus = ghnStatusObj.toString();
                    String newStatus = mapGHNStatusToSystem(ghnStatus);

                    System.err.println("  GHN Status: " + ghnStatus + " ➝ Mapped: " + newStatus);

                    if (newStatus != null && !newStatus.equals(currentStatus)) {
                        System.err.println("  ✅ Cập nhật trạng thái đơn hàng từ " + currentStatus + " ➝ " + newStatus);
                        orderDTO.setStatus(newStatus);
                        if(newStatus.equals("DaGiao")){
                            transactionService.updateStatusByOrderId(orderDTO.getOrderID());
                            voucherService.addVoucherCustomerOrderSuccess(orderDTO.getOrderID());

                        }
                        saveOrderIds.add(orderDTO);
                    } else {
                        System.err.println("  ⚠️ Không cần cập nhật hoặc trạng thái không thay đổi.");
                    }
                } else {
                    System.err.println("  ❌ Không tìm thấy shippingStatus trong phản hồi GHN.");
                }
            }
        }

        if (!saveOrderIds.isEmpty()) {
            orderService.saveOrders(saveOrderIds);
            System.err.println("💾 Đã cập nhật trạng thái cho " + saveOrderIds.size() + " đơn hàng.");
        }

        return ResponseEntity.ok(orderDTOs);
    }


    @GetMapping("/opulentia_user/orders/{orderId}")
    public ResponseEntity<OrderDetailDTO> getOrder(@PathVariable Integer orderId) {
        String accountId = AuthUtil.getAccountID();
        Order order = orderService.findOrderByID(orderId);

        if (accountId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if (!orderService.existsByOrderIDAndCustomer_CustomerId(orderId, accountId)) {
            return ResponseEntity.badRequest().build();
        }

        OrderDetailDTO dto = orderService.getOrderDetailByOrderID(orderId);

        if(order.getShippingCode()==null) {
            return ResponseEntity.ok(dto);
        }

        Map<String, Object> statusInfo = orderService.getOrderStatus(order.getShippingCode());
        System.err.println("shippingStatus = " + statusInfo.get("shippingStatus"));
        System.err.println("updatedTime = " + statusInfo.get("updatedTime"));


        dto.setShippingStatus(statusInfo.get("shippingStatus").toString());
        if(statusInfo.get("updatedTime")!=null){
            dto.setUpdatedTime(statusInfo.get("updatedTime").toString());
        }else dto.setUpdatedTime("");


        // Parse trackingHistory
        List<Map<String, String>> rawHistory = (List<Map<String, String>>) statusInfo.get("trackingHistory");
        if (rawHistory != null) {
            List<ShippingLogDTO> logs = rawHistory.stream()
                    .map(entry -> new ShippingLogDTO(entry.get("status"), entry.get("updatedTime")))
                    .collect(Collectors.toList());
            dto.setTrackingHistory(logs);
        }

        return ResponseEntity.ok(dto);
    }


    @GetMapping("/opulentia_user/orders/findByKeyword/{keyword}")
    public ResponseEntity<List<OrderDTO>> findByKeyword(@PathVariable String keyword) {
        String accountId = AuthUtil.getAccountID();
        if (accountId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // Kiểm tra nếu keyword là số
        if (isNumeric(keyword)) {
            try {

                return ResponseEntity.ok(orderService.getOrdersByCustomerIdAndOrderID(accountId, keyword));
            } catch (NumberFormatException e) {
                // Trường hợp keyword quá lớn không thể parse -> fallback sang tìm theo chuỗi
            }
        }

        // Trường hợp là chuỗi (hoặc fallback nếu parse thất bại)
        return ResponseEntity.ok(orderService.getOrdersByKeyword(accountId, keyword));
    }

    private boolean isNumeric(String str) {
        return str != null && str.matches("\\d+");
    }


    private String mapGHNStatusToSystem(String ghnStatus) {
        return switch (ghnStatus) {
            case "ready_to_pick", "picking", "money_collect_picking" -> "SanSangGiao";
            case "picked", "storing", "transporting", "delivering", "money_collect_delivering" -> "ChoGiaoHang";
            case "delivered" -> "DaGiao";
            default -> null; // hoặc trạng thái mặc định
        };
    }
}
