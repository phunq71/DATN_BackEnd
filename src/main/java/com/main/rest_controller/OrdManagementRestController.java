package com.main.rest_controller;

import com.main.dto.FacilityOrdManagerDTO;
import com.main.dto.OrdManagement_OrderDTO;
import com.main.dto.OrdManagement_ProductDTO;
import com.main.entity.Facility;
import com.main.entity.Order;
import com.main.service.FacilityService;
import com.main.service.OrderService;
import com.main.service.ProductService;
import com.main.utils.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
public class OrdManagementRestController {
    @Autowired
    FacilityService facilityService;
    @Autowired
    OrderService orderService;
    @Autowired
    ProductService productService;

    @GetMapping("/opulentia_admin/area")
    public ResponseEntity<List<FacilityOrdManagerDTO>> getFacilitiesByAccount() {
        String role = AuthUtil.getRole();
        if (Objects.equals(role, "ROLE_ADMIN")) {
            System.out.println(role);
            List<FacilityOrdManagerDTO> facility = facilityService.getShop();
            return ResponseEntity.ok(facility);
        } else if (Objects.equals(role, "ROLE_AREA")) {

        }
        return ResponseEntity.status(401).build();
    }

    @GetMapping("/opulentia_admin/order/{pageNumber}")
    public ResponseEntity<Page<OrdManagement_OrderDTO>> getFacilities
                            (@PathVariable Integer pageNumber,
                             @RequestParam(required = false) String areaId,
                             @RequestParam(required = false) String storeId,
                             @RequestParam(required = false) String day,
                             @RequestParam(required = false) String status,
                             @RequestParam(required = false) String orderType,
                             @RequestParam(required = false) String search) {
        System.err.println(areaId+" "+ storeId+" "+day+" "+status+" "+orderType+" "+search+" "+pageNumber);
        if (areaId == null) {
            System.out.println("❌ areaId là null thực sự");
        } else if (areaId.isEmpty()) {
            System.out.println("⚠️ areaId là chuỗi rỗng");
        } else if (areaId.equals("null")) {
            System.out.println("⚠️ areaId là chuỗi 'null'");
        } else {
            System.out.println("✅ areaId = " + areaId);
        }
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;
        if (day != null && !day.equals("")) {
            LocalDate localDate = LocalDate.parse(day); // parse thành LocalDate
            startDate = localDate.atStartOfDay();       // 2025-07-27T00:00
            endDate = localDate.plusDays(1).atStartOfDay(); // 2025-07-28T00:00
        }
        String role = AuthUtil.getRole();
//        System.err.println(startDate+" "+endDate);
        if (Objects.equals(role, "ROLE_ADMIN")) {
            Pageable pageable = PageRequest.of(pageNumber, 7);
            Page<OrdManagement_OrderDTO> orderDTOS = orderService.getOrders(startDate, endDate
                    , status, storeId, areaId, pageable);
            orderDTOS.forEach(orderDTO -> {
                Integer orderID = orderDTO.getOrderID(); // Giả sử có phương thức này
                List<OrdManagement_ProductDTO> productDTOS = orderService.getProductsByOrderID(orderID);
                orderDTO.setProducts(productDTOS); // Giả sử có setter này
            });
            System.err.println("Xem danh sách trả về " + orderDTOS.getContent());
            return ResponseEntity.ok(orderDTOS);
        } else if (Objects.equals(role, "ROLE_AREA")) {

        }
        return ResponseEntity.status(401).build();
    }

    @PostMapping("/opulentia_admin/order/confirm")
    public ResponseEntity<?> confirmOrder(@RequestBody Map<String, String> request) {
        try {
            Integer orderId = Integer.parseInt(request.get("orderID"));
            System.out.println("Order ID nè: " + orderId);
            Order o = orderService.findOrderByID(orderId);
            if (o == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ Không tìm thấy đơn hàng ID = " + orderId);
            }

            String status = o.getStatus();
            // Các trạng thái không được cập nhật
            List<String> khongChoPhepCapNhat = Arrays.asList("ChuanBiDon", "SanSangGiao", "DaGiao", "ChoGiaoHang");

            if ("ChoXacNhan".equals(status)) {
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
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("❌ orderID không hợp lệ");
        }
    }


}

