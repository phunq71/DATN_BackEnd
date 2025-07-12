package com.main.rest_controller;

import com.main.dto.CustomerDTO;
import com.main.dto.CustomerRegisterDTO;
import com.main.service.CustomerService;
import com.main.service.MailService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@RestController
@RequiredArgsConstructor
public class CustomerRegisterRestController {
    private final CustomerService customerService;
    private final MailService mailService;

    // 🔹 Bước 1: Gửi OTP và lưu DTO vào session
    @PostMapping("/opulentia/send-otp-register")
    public ResponseEntity<?> sendOtpForRegister(@RequestBody CustomerRegisterDTO dto, HttpSession session) {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("❗ Mật khẩu xác nhận không khớp.");
        }

        // Giới hạn thời gian gửi lại OTP (60 giây)
        Long lastSentTime = (Long) session.getAttribute("lastOtpSentTimeRegister");
        if (lastSentTime != null && System.currentTimeMillis() - lastSentTime < 60_000) {
            return ResponseEntity.badRequest().body("⏳ Vui lòng chờ ít nhất 60 giây trước khi gửi lại mã OTP.");
        }

        // Sinh OTP và gửi email
        int otp = 100000 + new Random().nextInt(900000);
        mailService.sendOTP(dto.getEmail(), otp);

        // Lưu vào session
        session.setAttribute("registerOtp", otp);
        session.setAttribute("registerOtpDTO", dto);
        session.setAttribute("registerOtpTime", System.currentTimeMillis());
        session.setAttribute("lastOtpSentTimeRegister", System.currentTimeMillis());

        return ResponseEntity.ok("✅ Mã OTP đã được gửi đến email của bạn.");
    }

    // 🔹 Bước 2: Xác nhận OTP và thực hiện đăng ký
    @PostMapping("/opulentia/confirm-otp-register")
    public ResponseEntity<?> confirmOtpAndRegister(@RequestParam("otp") int otp, HttpSession session) {
        Integer sessionOtp = (Integer) session.getAttribute("registerOtp");
        CustomerRegisterDTO dto = (CustomerRegisterDTO) session.getAttribute("registerOtpDTO");
        Long otpTime = (Long) session.getAttribute("registerOtpTime");

        // Kiểm tra timeout OTP (hết hạn sau 5 phút)
        if (otpTime == null || System.currentTimeMillis() - otpTime > 5 * 60 * 1000) {
            session.removeAttribute("registerOtp");
            session.removeAttribute("registerOtpDTO");
            session.removeAttribute("registerOtpTime");
            session.removeAttribute("lastOtpSentTimeRegister");
            return ResponseEntity.badRequest().body("❌ Mã OTP đã hết hạn. Vui lòng đăng ký lại.");
        }

        if (sessionOtp == null || dto == null) {
            return ResponseEntity.badRequest().body("❗ Không tìm thấy dữ liệu đăng ký. Vui lòng thử lại.");
        }

        if (otp != sessionOtp) {
            return ResponseEntity.badRequest().body("❌ Mã OTP không đúng.");
        }

        boolean success = customerService.saveCustomerRegister(dto);
        if (!success) {
            return ResponseEntity.internalServerError().body("❗ Đăng ký thất bại. Vui lòng thử lại sau.");
        }

        // Xoá session sau khi đăng ký thành công
        session.removeAttribute("registerOtp");
        session.removeAttribute("registerOtpDTO");
        session.removeAttribute("registerOtpTime");
        session.removeAttribute("lastOtpSentTimeRegister");

        return ResponseEntity.ok("🎉 Đăng ký thành công. Vui lòng đăng nhập để sử dụng dịch vụ.");
    }
}
