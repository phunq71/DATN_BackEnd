package com.main.rest_controller;

import com.main.dto.DisableAccountDTO;
import com.main.entity.Account;
import com.main.repository.AccountRepository;
import com.main.service.AccountService;
import com.main.service.MailService;
import com.main.utils.AuthUtil;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/opulentia_user")
@RequiredArgsConstructor
public class DisableAccountRestController {
    private final AccountService accountService;
    private final MailService mailService;
    private final AccountRepository accountRepository;
    String accountId = "";

    //gửi otp
    @PostMapping("/send-otp-deactivate")
    public ResponseEntity<?> sendOtpForDeactivate(HttpSession session) {
        accountId = AuthUtil.getAccountID();
        if (accountId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("❌ Bạn cần đăng nhập.");
        }

        // Kiểm tra còn đơn hàng trước
        if (accountService.hasPendingOrders(accountId)) {
            return ResponseEntity.badRequest().body("❌ Bạn còn đơn hàng chưa hoàn tất.");
        }

        Optional<Account> accOpt = accountRepository.findById(accountId);
        if (accOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("❗ Không tìm thấy tài khoản.");
        }

        String email = accOpt.get().getEmail();

        // Chặn gửi liên tục
        Long lastSentTime = (Long) session.getAttribute("lastOtpSentTimeDeactivate");
        if (lastSentTime != null && System.currentTimeMillis() - lastSentTime < 60_000) {
            return ResponseEntity.badRequest().body("⏳ Vui lòng chờ ít nhất 60 giây.");
        }

        int otp = 100000 + new Random().nextInt(900000);
        mailService.sendOTP(email, otp);

        session.setAttribute("deactivateOtp", otp);
        session.setAttribute("deactivateOtpTime", System.currentTimeMillis());

        return ResponseEntity.ok("✅ Mã OTP đã được gửi đến email.");
    }


    @PostMapping("/confirm-deactivate")
    public ResponseEntity<?> confirmOtpAndDeactivate(@RequestBody DisableAccountDTO dto, HttpSession session) {
        accountId = AuthUtil.getAccountID();
        if (accountId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("❌ Bạn cần đăng nhập.");
        }

        Integer otp = (Integer) session.getAttribute("deactivateOtp");
        Long otpTime = (Long) session.getAttribute("deactivateOtpTime");
        if (otp == null || otpTime == null || dto.getOtpCode() == null) {
            return ResponseEntity.badRequest().body("❗ Mã OTP không hợp lệ hoặc thiếu.");
        }

        // Check hết hạn
        if (System.currentTimeMillis() - otpTime > 5 * 60 * 1000) {
            session.removeAttribute("deactivateOtp");
            session.removeAttribute("deactivateOtpTime");
            return ResponseEntity.badRequest().body("❌ Mã OTP đã hết hạn.");
        }

        if (otp.intValue() != dto.getOtpCode()) {
            return ResponseEntity.badRequest().body("❌ Mã OTP không đúng.");
        }

        // Clear session OTP
        session.removeAttribute("deactivateOtp");
        session.removeAttribute("deactivateOtpTime");

        return ResponseEntity.ok("✅ Vô hiệu hóa tài khoản thành công!");
    }

    @PostMapping("/confirm-return")
    public ResponseEntity<?> confirmReturn() {
        boolean success = accountService.deactivateAccount(accountId);
        if (!success) {
            return ResponseEntity.internalServerError().body("❗ Vô hiệu hóa thất bại.");
        }
        return ResponseEntity.ok("✅ Vô hiệu hóa tài khoản thành công!");
    }

}
