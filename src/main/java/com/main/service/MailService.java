package com.main.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class MailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Async
    public void sendOTP(String to, int randomNumber) {
        try {
            String content = """
                <div style="font-family: Arial, sans-serif; padding: 20px; color: #333;">
                    <p style="font-size: 28px; font-weight: bold; color: #e74c3c; margin-bottom: 16px;">
                        🔐 Mã OTP của bạn: %d
                    </p>
                    <h3 style="color: #2c3e50;">Thông báo xác thực</h3>
                    <p>Xin chào,</p>
                    <p>Bạn (hoặc ai đó) vừa yêu cầu mã OTP để xác thực thao tác trên hệ thống.</p>
                    <p>Mã có hiệu lực trong 5 phút. Vui lòng không chia sẻ mã này cho bất kỳ ai.</p>
                    <br>
                    <p>Nếu bạn không thực hiện yêu cầu này, có thể bỏ qua email này.</p>
                    <hr style="margin: 20px 0;">
                    <p>Trân trọng, Opulentia</p>
                    <p><strong>Đội ngũ hỗ trợ kỹ thuật</strong></p>
                </div>
                """.formatted(randomNumber);

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("huyen.ngocharveynash@gmail.com");
            helper.setTo(to);
            helper.setSubject("🔐 Opulentia " + randomNumber); // để preview mail dễ thấy hơn
            helper.setText(content, true); // HTML

            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

}

