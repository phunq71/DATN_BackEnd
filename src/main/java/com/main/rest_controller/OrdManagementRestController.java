package com.main.rest_controller;

import com.main.dto.*;
import com.main.entity.*;
import com.main.repository.CustomerRepository;
import com.main.repository.LogOrderRepository;
import com.main.repository.OrderRepository;
import com.main.repository.StaffRepository;
import com.main.service.*;
import com.main.utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrdManagementRestController {
    private final FacilityService facilityService;
    private final OrderService orderService;
    private final VoucherService voucherService;
    private final TransactionService transactionService;
    private final AccountService accountService;
    private final StaffService staffService;
    private final LogOrderRepository logOrderRepository;
    private final StaffRepository staffRepository;
    private final CustomerService customerService;
    private final CustomerRepository customerRepository;
    private final MailService mailService;
    private final OrderRepository orderRepository;

    @GetMapping("/admin/area")
    public ResponseEntity<List<FacilityOrdManagerDTO>> getFacilitiesByAccount() {
        String role = AuthUtil.getRole();
        String accountId = AuthUtil.getAccountID();
        if (Objects.equals(role, "ROLE_ADMIN")) {
            System.out.println(role);
            List<FacilityOrdManagerDTO> facility = facilityService.getShop();
            return ResponseEntity.ok(facility);
        } else if (Objects.equals(role, "ROLE_AREA")) {
            System.err.println(role);
            List<FacilityOrdManagerDTO> facility = facilityService.getShopByManager_ID(accountId);
            System.err.println("Size fac: "+facility.size());
            return ResponseEntity.ok(facility);
        } else if (Objects.equals(role, "ROLE_STAFF")) {
            List<FacilityOrdManagerDTO> facility = facilityService.getShopByStaffID(accountId);
            return ResponseEntity.ok(facility);
        }
        return ResponseEntity.status(401).build();
    }

    @GetMapping("/admin/order/{pageNumber}")
    public ResponseEntity<Page<OrdManagement_OrderDTO>> getFacilities(
            @PathVariable Integer pageNumber,
            @RequestParam(required = false) String areaId,
            @RequestParam(required = false) String storeId,
            @RequestParam(required = false) List<String> day,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String orderType,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String orderId) {
        System.out.println("🙉🙉🙉🙉🙉🙉🙉");
        if (status != null && (status.equals("ChoGiaoHang") || status.equals("DaGiao") || status.equals("SanSangGiao"))) {
            statusOrderGHN(); // nếu bạn cần đồng bộ đơn hàng GHN
        }

        LocalDateTime startDate = null;
        LocalDateTime endDate = null;

        if (day != null && !day.isEmpty()) {
            // Nếu FE gửi ?day=2025-08-19&day=2025-08-21
            String firstDay  = (day.size() > 0) ? day.get(0) : null;
            String secondDay = (day.size() > 1) ? day.get(1) : null;

            LocalDate start;
            LocalDate end;

            if (firstDay == null || firstDay.isBlank()) {
                // Nếu ngày đầu null hoặc rỗng thì lấy ngày sau
                start = (secondDay != null && !secondDay.isBlank()) ? LocalDate.parse(secondDay) : null;
            } else {
                start = LocalDate.parse(firstDay);
            }

// Xử lý end
            if (secondDay == null || secondDay.isBlank()) {
                end = start;
            } else {
                end = LocalDate.parse(secondDay);
            }

            startDate = start.atStartOfDay();
            endDate   = end.atTime(LocalTime.MAX);
        }


        Integer Orderid = null;
        if(orderId!=null && !orderId.equals("")) {
            Orderid = extractOrderId(orderId);
        }
        Pageable pageable = PageRequest.of(pageNumber, 7);
        Page<OrdManagement_OrderDTO> orderDTOS = orderService.getOrders(startDate, endDate
                , status, storeId, areaId,Orderid, pageable);

        orderDTOS.forEach(orderDTO -> {
            Integer orderID = orderDTO.getOrderID();
            List<OrdManagement_ProductDTO> productDTOS = orderService.getProductsByOrderID(orderID);
            orderDTO.setProducts(productDTOS);
        });

        List<OrdManagement_OrderDTO> dtoList = orderDTOS.getContent().stream().map(dto -> {
            String orderCode = dto.getShippingCode();
            if (orderCode != null && !orderCode.isBlank()) {
                try {
                    Map<String, Object> ghnStatus = orderService.getOrderStatus(orderCode);
                    String statuss =(String) ghnStatus.get("shippingStatus");
                    dto.setStatusGHN(convertShippingStatusToVietnamese(statuss));
                    dto.setUpdatedTimeGHN((String) ghnStatus.get("updatedTime"));
                } catch (Exception e) {
                    dto.setStatusGHN("Lỗi lấy trạng thái");
                    dto.setUpdatedTimeGHN(null);
                }
            } else {
                if(dto.getStatus().equals("DaGiao")) {
                    dto.setStatusGHN("Đơn hàng thành công");
                }else {
                    dto.setStatusGHN("Chưa tạo GHN");
                }

                dto.setUpdatedTimeGHN(null);
            }
            return dto;
        }).collect(Collectors.toList());

        Page<OrdManagement_OrderDTO> finalPage = new PageImpl<>(dtoList, pageable, orderDTOS.getTotalElements());
        return ResponseEntity.ok(finalPage);
    }

    @PostMapping("/admin/order/confirm")
    public ResponseEntity<?> confirmOrder(@RequestBody Map<String, String> request) {
        String role = AuthUtil.getRole();
        String accountId = AuthUtil.getAccountID();
        Staff staff = staffService.getStaffByAccountID(accountId);
        try {
            Integer orderId = Integer.parseInt(request.get("orderID"));
            System.out.println("Order ID nè: " + orderId);

            Order o = orderService.findOrderByID(orderId);
            if (o == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ Không tìm thấy đơn hàng ID = " + orderId);
            }
            LogOrders logOrders = new LogOrders();
            logOrders.setStaff(staff);
            logOrders.setOrder(o);
            logOrders.setContent("Đơn hàng đã được xác nhận");
            logOrders.setUpdateAt(LocalDateTime.now());
            logOrderRepository.save(logOrders);

            String status = o.getStatus();
            // Các trạng thái không được cập nhật
            List<String> khongChoPhepCapNhat = Arrays.asList("ChuanBiDon", "SanSangGiao", "DaGiao", "ChoGiaoHang");
            if (Objects.equals(role, "ROLE_ADMIN") || Objects.equals(role, "ROLE_AREA")) {
                if ("ChoXacNhan".equals(status)) {
                    o.setStaff(staff);
                    o.setStatus("ChuanBiDon");
                    o.setUpdateStatusAt(LocalDateTime.now());
                    orderService.save(o);
                    return ResponseEntity.ok("✅ Đã cập nhật trạng thái đơn hàng thành 'ChuanBiDon'");
                } else if (khongChoPhepCapNhat.contains(status)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("❌ Đơn hàng đang ở trạng thái '" + status + "' nên không thể xác nhận lại");
                } else {
                    return ResponseEntity.ok("⚠️ Trạng thái hiện tại ('" + status + "') không nằm trong luồng xử lý cho xác nhận");
                }
            } else {
                return ResponseEntity.ok("⚠️ Bạn không có quyền xác nhận đơn");
            }

            // thêm lưu log


        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("❌ orderID không hợp lệ");
        }
    }
    @PostMapping("/admin/order/ready-to-ship")
    public ResponseEntity<?> markReadyToShip(@RequestBody GhnOrderRequestDTO payload) {
        System.out.println("order tu pay load: "+payload.getOrderID());
        try {
            // Gọi service xử lý tạo đơn hàng GHN
            String ghnOrderCode = orderService.createGhnOrder(payload);

            // Trả về mã đơn hàng GHN vừa tạo (nếu cần hiển thị ở frontend)
            return ResponseEntity.ok("Đơn hàng GHN đã tạo thành công. Mã GHN: " + ghnOrderCode);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Tạo đơn GHN thất bại: " + e.getMessage());
        }
    }
    private List<OrderDetailDTO> statusOrderGHN() {
        List<OrderDetailDTO> orderDetailDTOS = orderService.getAllOrderIdShippingCodes(); // tại hàm này 💾💾💾
        int updatedCount = 0;
        System.out.println("Test size 👉👉👉: " + orderDetailDTOS.size());
        for (OrderDetailDTO dto : orderDetailDTOS) {
            String shippingCode = dto.getShippingCode();

            if (shippingCode == null || shippingCode.isBlank()) continue;

            OrderDetailDTO orderDetail = orderService.getOrderIdByShippingCodes(shippingCode);
            if (orderDetail == null) continue;


            try {
                Map<String, Object> ghnResult = orderService.getOrderStatus(shippingCode);
                String ghnStatus = (String) ghnResult.get("shippingStatus");
                String systemStatus = mapGHNStatusToSystem(ghnStatus);

                String currentStatus = orderDetail.getStatus();
                if (systemStatus != null && !systemStatus.equals(currentStatus)) {
                    Order order = orderService.findOrderByID(orderDetail.getOrderID());
                    order.setStatus(systemStatus);
                    LogOrders logOrders = new LogOrders();
                    logOrders.setStaff(null);
                    logOrders.setOrder(order);

                    logOrders.setUpdateAt(LocalDateTime.now());

                    if (systemStatus.equals("ChoGiaoHang")){
                        logOrders.setContent("Shipper đã lấy đơn, đợi giao hàng");
                    }else if(systemStatus.equals("DaGiao")){
                        logOrders.setContent("Đơn hàng đã được giao thành công");
                    }
                    logOrderRepository.save(logOrders);
                    orderService.save(order);
                    updatedCount++;
                    if(systemStatus.equals("DaGiao")){
                        transactionService.updateStatusByOrderId(dto.getOrderID());
                        voucherService.addVoucherCustomerOrderSuccess(dto.getOrderID());
                        customerService.updateRankByCustomerId(order.getCustomer());
                    }
                }

            } catch (Exception e) {
                System.err.println("Lỗi cập nhật đơn " + shippingCode + ": " + e.getMessage());
            }
        }

        System.err.println("Tổng số đơn đã cập nhật trạng thái: " + updatedCount);
        return orderDetailDTOS;
    }
    private String mapGHNStatusToSystem(String ghnStatus) {
        return switch (ghnStatus) {
            case "ready_to_pick", "picking", "money_collect_picking" -> "SanSangGiao";
            case "picked", "storing", "transporting", "delivering", "money_collect_delivering" -> "ChoGiaoHang";
            case "delivered" -> "DaGiao";
            default -> null; // hoặc trạng thái mặc định
        };
    }
    public String convertShippingStatusToVietnamese(String status) {
        switch (status) {
            case "ready_to_pick": return "Sẵn sàng lấy hàng";
            case "picking": return "Đang lấy hàng";
            case "money_collect_picking": return "Đang thu tiền khi lấy hàng";
            case "picked": return "Đã lấy hàng";
            case "storing": return "Đã nhập kho";
            case "transporting": return "Đang luân chuyển";
            case "sorting": return "Đang phân loại";
            case "delivering": return "Đang giao hàng";
            case "money_collect_delivering": return "Đang thu tiền khi giao";
            case "delivery_fail": return "Giao hàng thất bại";
            case "delivered": return "Đã giao hàng";
            case "delivery_fail_retain": return "Giao thất bại, giữ lại";
            case "return": return "Đang chuyển hoàn";
            case "return_transporting": return "Chuyển hoàn - đang vận chuyển";
            case "return_sorting": return "Chuyển hoàn - đang phân loại";
            case "returning": return "Đang trả hàng";
            case "return_fail": return "Trả hàng thất bại";
            case "returned": return "Đã trả hàng";
            case "cancel": return "Đã hủy đơn";
            default: return "Không xác định";
        }
    }
    public Integer extractOrderId(String rawOrderId) {
        if (rawOrderId == null || rawOrderId.isBlank()) return null;

        // Loại bỏ mọi ký tự không phải số
        String numberOnly = rawOrderId.replaceAll("[^0-9]", ""); // VD: "#OD000123" → "000123"

        if (numberOnly.isEmpty()) return null;

        return Integer.parseInt(numberOnly); // 123
    }


    @PutMapping("/admin/orders11/reject")
    public ResponseEntity<?> rejectOrder(@RequestBody Map<String, String> body) {
        try {
            String orderId = body.get("orderId");
            String reason = body.get("reason");

            // Lấy số bằng regex
            String numberOnly = orderId.replaceAll("\\D", ""); // 0031037

            // Chuyển về int (tự động bỏ 0 đầu)
            int orderNumber = Integer.parseInt(numberOnly);
            System.out.println("Reject Order: " + orderId);
            System.out.println(orderNumber);
            System.out.println("Reason: " + reason);
            orderService.cancelOrder2(orderNumber, reason);
            mailService.sendRejectOrderEmail(orderId, reason
                    , orderRepository.findByOrderID(orderNumber).getCustomer().getAccount().getEmail()
                    , orderRepository.findByOrderID(orderNumber).getCustomer().getFullName() );
            return ResponseEntity.ok("Đơn hàng " + orderId + " đã bị từ chối với lý do: " + reason);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi hệ thống: " + e.getMessage());
        }
    }


    // Endpoint chấp nhận hủy đơn
    @PostMapping("/admin/order/cancelOrderCustomer")
    public ResponseEntity<?> chapnhanHuy(@RequestBody Map<String, String> payload) {
        try {
            String orderId = payload.get("orderId");

            System.out.println("ChapnhanHuy orderId: " + orderId);
//
            // Lấy số bằng regex
            String numberOnly = orderId.replaceAll("\\D", ""); // 0031037

            // Chuyển về int (tự động bỏ 0 đầu)
            int orderNumber = Integer.parseInt(numberOnly);

            orderService.cancelOrder2(orderNumber, "Huỷ đơn hàng theo ý khách");
            return  ResponseEntity.ok("Đơn hàng " );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi server");
        }
    }


}

