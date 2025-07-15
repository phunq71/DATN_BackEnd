package com.main.rest_controller;

import com.main.dto.CustomerDTO;
import com.main.entity.Customer;
import com.main.service.AccountService;
import com.main.service.CustomerService;
import com.main.utils.AuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
public class CustomerRestController {
    private final CustomerService customerService;
    private final AccountService accountService;

    public CustomerRestController(CustomerService customerService, AccountService accountService) {
        this.customerService = customerService;
        this.accountService = accountService;
    }

    @GetMapping("/opulentia_user/getCustomer")
    public ResponseEntity<CustomerDTO> getCustomer() {
        String accountId= AuthUtil.getAccountID();
        if (accountId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        CustomerDTO customer = customerService.getCustomerDTOByaccountId(accountId);
        customer.setIsOAuth2(accountService.isOAuth2(accountId));
        return ResponseEntity.ok(customer);
    }

    @PostMapping("/opulentia_user/checkPassword")
    public ResponseEntity<Boolean> checkPassword(@RequestBody Map<String, String> password) {
        String accountId= AuthUtil.getAccountID();
        if (accountId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String rawPassword = password.get("password");
        boolean checkPassword= accountService.checkPassword(accountId, rawPassword);
        return ResponseEntity.ok(checkPassword);

    }

    @PutMapping("/opulentia_user/changePassword")
    public ResponseEntity<Boolean> changePassword(@RequestBody Map<String, String> password) {
        String accountId = AuthUtil.getAccountID();
        if (accountId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String oldPassword = password.get("oldPassword");
        String newPassword = password.get("newPassword");
        String confirmPassword = password.get("confirmPassword");
        boolean isChanged = false;
        if (accountService.checkPassword(accountId, oldPassword) && newPassword.equals(confirmPassword)) {
            isChanged = accountService.newPassword(accountId, newPassword);
        }
        return ResponseEntity.ok(isChanged);
    }

    @PutMapping("/opulentia_user/saveCustomer")
    public ResponseEntity<?> saveCustomer(
            @RequestParam("fullName") String fullName,
            @RequestParam("gender") Boolean gender,
            @RequestParam("dob") LocalDate dob,
            @RequestParam("province") String province,
            @RequestParam("district") String district,
            @RequestParam("ward") String ward,
            @RequestParam("addressDetail") String addressDetail,
            @RequestParam(value = "imageAvt", required = false) MultipartFile avatar) {

        // 1. Authentication check
        String accountId = AuthUtil.getAccountID();
        if (accountId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            // 2. Validate fullName (không chứa số, ký tự đặc biệt hoặc icon)
            if (fullName == null || fullName.trim().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Họ và tên không được để trống");
            }

            String nameRegex = "^[\\p{L} \\p{M}'-]+$";
            if (!fullName.matches(nameRegex)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Họ và tên không được chứa số, ký tự đặc biệt hoặc biểu tượng");
            }

            // 3. Validate các phần địa chỉ
            String addressRegex = "^[\\p{L}0-9 ./\\-]+$";

            if (province == null || province.trim().isEmpty() || !province.matches(addressRegex)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Tỉnh/Thành phố không được chứa dấu phẩy hoặc ký tự đặc biệt");
            }

            if (district == null || district.trim().isEmpty() || !district.matches(addressRegex)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Quận/Huyện không được chứa dấu phẩy hoặc ký tự đặc biệt");
            }

            if (ward == null || ward.trim().isEmpty() || !ward.matches(addressRegex)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Phường/Xã không được chứa dấu phẩy hoặc ký tự đặc biệt");
            }

            if (addressDetail == null || addressDetail.trim().isEmpty() || !addressDetail.matches(addressRegex)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Địa chỉ chi tiết không được chứa dấu phẩy hoặc ký tự đặc biệt");
            }

            // 4. Kết hợp địa chỉ đầy đủ (sử dụng dấu cách thay vì dấu phẩy)
            String fullAddress = String.join(", ", addressDetail, ward, district, province);

            // 5. Validate file ảnh (nếu có)
            if (avatar != null && !avatar.isEmpty()) {
                if (avatar.getSize() > 10 * 1024 * 1024) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "File ảnh không được vượt quá 10MB");
                }
                if (!avatar.getContentType().startsWith("image/")) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "File tải lên phải là hình ảnh");
                }
            }

            // 6. Tạo và lưu thông tin khách hàng
            CustomerDTO customerDTO = new CustomerDTO(
                    accountId,
                    fullName,
                    gender,
                    "",
                    dob,
                    null,
                    addressDetail,
                    ward,
                    district,
                    province,
                    null
            );

            CustomerDTO savedCustomer = customerService.saveCustomer(customerDTO, avatar);

            return ResponseEntity.ok(savedCustomer);

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau."
            );
        }
    }
    @GetMapping("/opulentia_user/phoneNumber")
    public ResponseEntity<String> getPhoneNumber() {
        String accountId= AuthUtil.getAccountID();
        if (accountId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Customer customer = customerService.findByAccountID(accountId);
        return ResponseEntity.ok(customer.getPhone());
    }
    @PutMapping("/opulentia_user/changePhone")
    public ResponseEntity<String> updatePhoneNumber(@RequestBody Map<String, String> request) {
        String accountId = AuthUtil.getAccountID();
        if (accountId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Chưa đăng nhập");
        }

        String newPhone = request.get("newPhoneNumber");
        if (newPhone == null || !newPhone.matches("^0\\d{9}$")) {
            return ResponseEntity.badRequest().body("Số điện thoại không hợp lệ");
        }

        Customer customer = customerService.findByAccountID(accountId);
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy khách hàng");
        }

        customer.setPhone(newPhone);
        customerService.save(customer); // hoặc update nếu bạn dùng custom

        return ResponseEntity.ok("Cập nhật thành công");
    }
    @GetMapping("/opulentia_user/qrImage")
    public ResponseEntity<byte[]> generateQRCode(HttpServletRequest request) throws Exception {
        String accountId = AuthUtil.getAccountID();
        System.out.println("co acoount rrrrrrrr"+accountId);
        byte[] qrImage = customerService.generateQRCode(accountId, 250, 250);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(qrImage);
    }
}
