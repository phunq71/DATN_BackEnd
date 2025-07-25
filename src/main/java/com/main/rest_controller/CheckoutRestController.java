package com.main.rest_controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.main.dto.OrderPreviewDTO;
import com.main.dto.VoucherOrderDTO;
import com.main.entity.Customer;
import com.main.entity.Voucher;
import com.main.mapper.VoucherMapper;
import com.main.repository.VoucherRepository;
import com.main.service.CustomerService;
import com.main.service.FacilityService;
import com.main.service.OrderService;
import com.main.service.VoucherService;
import com.main.utils.AuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/opulentia_user")
@RequiredArgsConstructor
public class CheckoutRestController {

    private final OrderService orderService;

    private final CustomerService customerService;
    private final VoucherService voucherService;


    @PostMapping("/datacheckout")
    public ResponseEntity<?> getDataCheckout(@RequestBody List<OrderPreviewDTO> itemIds) {
        try {
            Map<String, Object> dataCheckout = new HashMap<>();
            dataCheckout = orderService.getOrderPreviewData(itemIds);
            // ✅ In ra danh sách itemIds từ FE
            System.out.println("📥 Danh sách item_id nhận được từ FE:");
            itemIds.forEach(id -> System.out.println(" - " + id));

            dataCheckout.put("message", "Đã nhận được danh sách itemIds thành công");
            dataCheckout.put("itemIds", itemIds);

            return ResponseEntity.ok(dataCheckout);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi xử lý yêu cầu");
        }
    }


    // Cập nhật địa chỉ mặc định của tài khoản
    @PostMapping("/update/address")
    public ResponseEntity<?> updateAddress(@RequestBody Map<String, String> body) {
        if (body == null || !body.containsKey("customerAddressIdGHN") || !body.containsKey("customerAddress")) {
            return ResponseEntity.badRequest().body("Thiếu dữ liệu địa chỉ.");
        }

        String addressIdGHN = body.get("customerAddressIdGHN");
        String addressText = body.get("customerAddress");

        Boolean result = customerService.updateAddrBoolean(addressText, addressIdGHN);

        if (result) {
            return ResponseEntity.ok().build();
        }else{
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/suggested-vouchers")
    public ResponseEntity<Map<String, VoucherOrderDTO>> getSuggestedVouchersInline(
            @RequestParam BigDecimal totalAmount
    ) {
        String accountId = AuthUtil.getAccountID();
        Customer customer = customerService.findByAccountID(accountId);
        String membershipId = customer.getMembership().getMembershipId();

        // Gọi service đã viết sẵn
        VoucherOrderDTO hauMai1 = voucherService.getNearestVoucherToReach(membershipId, totalAmount);
        VoucherOrderDTO hauMai2 = voucherService.getBestMatchedVoucher(membershipId, totalAmount);

        Map<String, VoucherOrderDTO> result = new HashMap<>();
        result.put("hauMai1", hauMai1);
        result.put("hauMai2", hauMai2);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/order/add")
    public ResponseEntity<Map<String, String>> checkout(@RequestBody Map<String, Object> checkoutInfo) {
        boolean result = orderService.addOrderCustomer(checkoutInfo);

        Map<String, String> response = new HashMap<>();
        response.put("message", result ? "Đã nhận checkoutInfo thành công" : "Xử lý thất bại");

        HttpStatus status = result ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(status).body(response);
    }







}


