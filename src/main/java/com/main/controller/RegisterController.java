package com.main.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/opulentia")
public class RegisterController {
    @GetMapping("/register/otp")
    public String showOtpPage(@RequestParam String email, Model model) {
        model.addAttribute("email", email);
        return "View/register-opt"; // Trả về template register-opt.html
    }

//    @PostMapping("/register/verify-otp")
//    public String verifyOtp(@RequestParam String otp,
//                            @RequestParam String email,
//                            HttpSession session,
//                            RedirectAttributes redirectAttributes) {
//        // Lấy OTP từ session
//        String storedOtp = (String) session.getAttribute("registerOtp");
//        Long expiryTime = (Long) session.getAttribute("otpExpiry");
//
//        // Kiểm tra OTP
//        if (storedOtp == null || !storedOtp.equals(otp)) {
//            redirectAttributes.addAttribute("message", "Mã OTP không đúng");
//            return "redirect:/opulentia/register/otp?email=" + encodeURIComponent(email);
//        }
//
//        if (System.currentTimeMillis() > expiryTime) {
//            redirectAttributes.addAttribute("message", "Mã OTP đã hết hạn");
//            return "redirect:/opulentia/register/otp?email=" + encodeURIComponent(email);
//        }
//
//        // OTP hợp lệ, hoàn tất đăng ký
//        CustomerRegistrationDto customerData = (CustomerRegistrationDto) session.getAttribute("customerData");
//        // TODO: Lưu thông tin đăng ký vào database
//
//        // Xóa session OTP
//        session.removeAttribute("registerOtp");
//        session.removeAttribute("otpExpiry");
//        session.removeAttribute("customerData");
//
//        return "redirect:/opulentia/auth?registerSuccess=true";
//    }

//    @PostMapping("/send-otp-register")
//    @ResponseBody
//    public ResponseEntity<?> sendOtpRegister(@RequestBody Map<String, String> request, HttpSession session) {
//        String email = request.get("email");
//        if (email == null || !isValidEmail(email)) {
//            return ResponseEntity.badRequest().body("Email không hợp lệ");
//        }
//
//        // Generate OTP
//        String otp = String.format("%06d", new Random().nextInt(999999));
//
//        // Save OTP to session
//        session.setAttribute("registerOtp", otp);
//        session.setAttribute("otpExpiry", System.currentTimeMillis() + 300000); // 5 phút
//        session.setAttribute("registerEmail", email);
//
//        // Send OTP via email
//        emailService.sendEmail(email, "Mã OTP đăng ký", "Mã OTP của bạn là: " + otp);
//
//        return ResponseEntity.ok().build();
//    }
//
//    @PostMapping("/confirm-otp-register")
//    @ResponseBody
//    public ResponseEntity<?> confirmOtpRegister(@RequestBody CustomerRegistrationDto registrationData,
//                                                HttpSession session) {
//        // Verify OTP first
//        String storedOtp = (String) session.getAttribute("registerOtp");
//        Long expiryTime = (Long) session.getAttribute("otpExpiry");
//        String sessionEmail = (String) session.getAttribute("registerEmail");
//
//        if (storedOtp == null || !storedOtp.equals(registrationData.getOtp())
//                || !registrationData.getEmail().equals(sessionEmail)) {
//            return ResponseEntity.badRequest().body("Mã OTP không đúng");
//        }
//
//        if (System.currentTimeMillis() > expiryTime) {
//            return ResponseEntity.badRequest().body("Mã OTP đã hết hạn");
//        }
//
//        // OTP valid, process registration
//        try {
//            // TODO: Validate registration data
//            // TODO: Save user to database
//
//            // Clear session
//            session.removeAttribute("registerOtp");
//            session.removeAttribute("otpExpiry");
//            session.removeAttribute("registerEmail");
//
//            return ResponseEntity.ok("Đăng ký thành công");
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body("Đăng ký thất bại: " + e.getMessage());
//        }
//    }
}
