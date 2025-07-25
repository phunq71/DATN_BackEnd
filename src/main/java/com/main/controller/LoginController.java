package com.main.controller;

import com.main.entity.Account;
import com.main.repository.AccountRepository;
import com.main.service.MailService;
import com.main.utils.ValidationUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Optional;
import java.util.Random;

@Controller
public class LoginController {
    @Autowired
    MailService mailService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private HttpSession session;
    @Autowired
    private PasswordEncoder passwordEncoder;

    // chuyển đến form đăng nhập
	@GetMapping("/auth")
	public String login(){
        return "View/auth";
    }

    @GetMapping("/auth/login")
    public String login(@RequestParam(value = "username", required = false) String username,
                        @RequestParam(value = "error", required = false) String errorParam,
                        Model model) {

        if (errorParam != null) {
            model.addAttribute("error", "Sai email hoặc password!");
            model.addAttribute("messageLayout", "Đăng nhập thất bại!");
            model.addAttribute("status", "error");
        }

        model.addAttribute("username", username);

        return "View/auth";
    }


    // chuyển đến form quên mật khẩu
    @GetMapping("/auth/forgot-password")
    public String forgotPassword() {
        return "View/forgot-password";
    }

    // gửi mã OTP và chuyển đến form OTP
    @PostMapping("/auth/forgot-password")
    public String forgotPassword(@RequestParam("email") String email, Model model) {
        if (email == null || email.trim().isEmpty()) {
            model.addAttribute("message", "❗ Vui lòng nhập địa chỉ email.");
        }
        // Giới hạn thời gian gửi lại mã
        Long lastSentTime = (Long) session.getAttribute("lastOtpSentTime");
        if (lastSentTime != null && System.currentTimeMillis() - lastSentTime < 60 * 1000) { // 60 giây
            model.addAttribute("message", "⏳ Vui lòng chờ trước khi gửi lại mã OTP.");
            return "View/forgot-password-otp";
        }
        // Tìm tài khoản local (provider = null)
        Optional<Account> accOpt = accountRepository.findByEmailAndProviderIsNull(email);

        if (accOpt.isPresent()) {
            // Gửi mã OTP
            Random rand = new Random();
            int otp = 100000 + new Random().nextInt(900000);// Lưu vào session
            session.setAttribute("otp", otp);
            session.setAttribute("otpEmail", email);
            session.setAttribute("otpTime", System.currentTimeMillis());
            session.setAttribute("lastOtpSentTime", System.currentTimeMillis());
            model.addAttribute("email", email);
            // đảm bảo luôn có 6 chữ số
            mailService.sendOTP(email, otp); // giả sử bạn có hàm generate()
            return "View/forgot-password-otp"; // Hoặc chuyển sang trang nhập OTP
        }

        // Kiểm tra có phải tài khoản OAuth2 không
        boolean oauthExists = accountRepository.existsByEmailAndProviderIsNotNull(email);
        if (oauthExists) {
            model.addAttribute("message", "⚠️Email này đã liên kết với tài khoản Google hoặc Facebook. "
                    + "Vui lòng quay lại đăng nhập bằng phương thức đó.");
        } else {
            model.addAttribute("message", "❌ Email không tồn tại trong hệ thống.");
        }

        return "View/forgot-password";
    }

    @PostMapping("/auth/verify-otp")
    public String verifyOtp(@RequestParam("otp") String userInputOtp, HttpSession session, Model model) {
        Integer sessionOtp = (Integer) session.getAttribute("otp");
        String otpEmail = (String) session.getAttribute("otpEmail");
        Long otpTime = (Long) session.getAttribute("otpTime");

        // 1. Kiểm tra tồn tại
        if (sessionOtp == null || otpTime == null || otpEmail == null) {
            model.addAttribute("message", "❗ Phiên xác thực không hợp lệ hoặc đã hết hạn.");
            return "View/forgot-password";
        }

        // 2. Kiểm tra quá hạn (5 phút)
        long currentTime = System.currentTimeMillis();
        if ((currentTime - otpTime) > 5 * 60 * 1000) { // quá 5 phút
            session.removeAttribute("otp");
            model.addAttribute("message", "⏰ Mã OTP đã hết hạn. Vui lòng gửi lại mã mới.");
            return "View/forgot-password";
        }

        // 3. So khớp OTP
        if (userInputOtp.equals(sessionOtp.toString())) {
            // ✅ OTP đúng → chuyển sang form đặt lại mật khẩu
            model.addAttribute("email", otpEmail); // truyền lại để biết đặt lại mật khẩu cho ai
            return "View/reset-password"; // form đặt lại mật khẩu
        } else {
            model.addAttribute("message", "❌ Mã OTP không chính xác.");
            return "View/forgot-password-otp";
        }
    }

    @PostMapping("/auth/reset-password")
    public String handleResetPassword(
            @RequestParam("email") String email,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            Model model) {

        // 1. Kiểm tra mật khẩu trùng khớp
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("message", "❌ Mật khẩu xác nhận không trùng khớp.");
            model.addAttribute("email", email);
            return "View/reset-password";
        }

        // 2. Kiểm tra định dạng mật khẩu
        if (!ValidationUtils.isValidPassword(newPassword)) {
            model.addAttribute("message", "❗ Mật khẩu phải có ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt.");
            model.addAttribute("email", email);
            return "View/reset-password";
        }

        // 3. Tìm tài khoản
        Optional<Account> accOpt = accountRepository.findByEmailAndProviderIsNull(email);
        if (accOpt.isEmpty()) {
            model.addAttribute("message", "❌ Không tìm thấy tài khoản phù hợp.");
            return "View/reset-password";
        }

        Account acc = accOpt.get();

        // 4. Mã hóa và cập nhật mật khẩu mới
        String hashedPassword = passwordEncoder.encode(newPassword);
        acc.setPassword(hashedPassword);
        acc.setUpdateAt(LocalDate.now());
        accountRepository.save(acc);

        // 5. Thông báo thành công và chuyển về /auth
        model.addAttribute("message", "✅ Đặt lại mật khẩu thành công. <a href='/auth'>Quay lại trang đăng nhập</a>");
        return "View/reset-password";
    }

//    v3qJHiO/ffKD+oLXkCMnyImZ/TOm8Rds6IZ6Xd6Cp456J8Ogh+qUYoUchFN2FliZwdTJa1lbJ5tUUpnJQOx/Ew==
//    public static void main(String[] args) {
//        SecureRandom random = new SecureRandom();
//        byte[] key = new byte[64]; // 256-bit
//        random.nextBytes(key);
//        String base64Key = Base64.getEncoder().encodeToString(key);
//        System.out.println("Remember-me key: " + base64Key);
//    }
}
