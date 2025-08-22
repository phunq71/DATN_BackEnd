package com.main.rest_controller;

import com.main.dto.*;
import com.main.entity.Account;
import com.main.service.*;
import com.main.utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class CustomerManagementRestController {
    private final CustomerService customerService;
    private final OrderService orderService;
    private final ProductService productService;
    private final AccountService accountService;
    private final MailService mailService;
    private final MembershipService membershipService;
    private final ReviewService reviewService;
    @GetMapping("/admin/CustomerManager/{page}")
    public ResponseEntity<?> CustomerManager(@PathVariable int page,
                                             @RequestParam(required = false) String membershipId,
                                             @RequestParam(required = false) String idCustomer) {
        Page<CustomerManagementDTO> customers = customerService.getAllCustomer(membershipId,idCustomer,page);
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/admin/CustomerManager/Order/{customerId}/{page}")
    public ResponseEntity<?> CustomerManagerID(@PathVariable String customerId,
                                               @PathVariable int page
                                                ) {
        Page<CusManagement_orderDTO> customerOrder = orderService.getOrdersByCustomerId(customerId,page);
        System.err.println("customerOrder.getTotalPages():"+customerOrder.getTotalPages());
        if (customerOrder != null) {
            return ResponseEntity.ok(customerOrder);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/admin/CustomerManager/Products/{orderId}/{customerId}")
    public ResponseEntity<?> CustomerManagerOrderProduct(@PathVariable Integer orderId,
                                                         @PathVariable String customerId) {
        System.err.println("uustomerID"+customerId);
        List<CusManagement_productDTO> productDTOS = productService.findProductByOrderID(orderId, customerId);
        return ResponseEntity.ok(productDTOS);
    }

    @GetMapping("/admin/CustomerManager/lockAccount")
    public ResponseEntity<?> CustomerManagerLock(@RequestParam String idCustomer) {
        String role = AuthUtil.getRole();
        if (role.equals("ROLE_ADMIN")) {
            Optional<Account> acc = accountService.finByAccountId(idCustomer);
            if (acc.isPresent()) {
                acc.get().setStatus(false);
                acc.get().setUpdateAt(LocalDate.now());
                accountService.saveAccount(acc.get());
                mailService.sendAccountLockedEmail(acc.get().getEmail());
                return ResponseEntity.ok(true);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy tài khoản");
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn không có quyền thực hiện hành động này");
    }

    @GetMapping("/admin/CustomerManager/openAccount")
    public ResponseEntity<?> CustomerManagerOpenAccount(@RequestParam String idCustomer) {
        String role = AuthUtil.getRole();
        if (!"ROLE_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn không có quyền thực hiện hành động này");
        }

        Optional<Account> acc = accountService.finByAccountId(idCustomer);
        if (acc.isPresent()) {
            acc.get().setStatus(true);
            acc.get().setUpdateAt(LocalDate.now());
            accountService.saveAccount(acc.get());
            mailService.sendAccountOpenEmail(acc.get().getEmail());
            return ResponseEntity.ok(true);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy tài khoản");
    }

    @GetMapping("/admin/CustomerManager/deleteCustomer")
    public ResponseEntity<?> deleteCustomer(@RequestParam String idCustomer) {
        String role = AuthUtil.getRole();
        if (!"ROLE_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn không có quyền thực hiện hành động này");
        }
        boolean deleted = customerService.deleteCustomer(idCustomer);
        if (deleted) {
            return ResponseEntity.ok(true);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy khách hàng");
    }


    @GetMapping("/admin/CustomerManager/CustomerChart")
    public ResponseEntity<?> CustomerManager() {
        Map<String, Object> chartData =  customerService.findAllAddresses();
        return ResponseEntity.ok(chartData);
    }
    @GetMapping("/admin/CustomerManager/memberships")
    public List<MembershipDTO> getMemberships() {
        return membershipService.getAllMemberships();
    }

    @GetMapping("/admin/CustomerManager/avgReview")
    public ReviewStatsDTO getReviewStats() {
        return reviewService.getReviewStats();
    }
}
