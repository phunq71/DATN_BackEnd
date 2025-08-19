package com.main.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;
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
    @Async
    public void sendVoucherEmail(String to, String voucherCode, Integer discountDetail, LocalDateTime expiryDate) {
        try {
            String content = """
            <div style="font-family: Arial, sans-serif; padding: 20px; color: #333;">
                <h2 style="color: #27ae60;">🎁 Bạn nhận được một voucher từ Opulentia!</h2>
                <p>Chào bạn,</p>
                <p>Chúng tôi gửi tặng bạn một mã khuyến mãi đặc biệt:</p>
                <div style="font-size: 24px; font-weight: bold; background-color: #f1f1f1; 
                            padding: 15px; margin: 20px 0; border: 2px dashed #27ae60; 
                            text-align: center; color: #e74c3c;">
                    %s
                </div>
                
                <p><strong>Ưu đãi:</strong> %s</p>
                <p><strong>Hạn sử dụng:</strong> %s</p>
                
                <p>Hãy nhanh tay sử dụng mã này trước khi hết hạn nhé!</p>
                <hr style="margin: 20px 0;">
                <p>Trân trọng,<br>Đội ngũ Opulentia</p>
            </div>
        """.formatted(voucherCode, discountDetail,expiryDate != null? expiryDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")): " ");

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("huyen.ngocharveynash@gmail.com");
            helper.setTo(to);
            helper.setSubject("🎁 Nhận ngay voucher ưu đãi từ Opulentia!");
            helper.setText(content, true); // HTML content

            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Async
    public void sendAccountLockedEmail(String to) {
        try {
            String content = """
        <div style="font-family: Arial, sans-serif; padding: 20px; color: #333;">
            <h2 style="color: #e74c3c;">🔒 Tài khoản của bạn đã bị khóa</h2>
            <p>Chào bạn,</p>
            <p>Chúng tôi xin thông báo rằng tài khoản của bạn trên <strong>Opulentia</strong> đã bị <strong>khóa tạm thời</strong> vì lý do vi phạm hoặc bảo mật.</p>
            
            <div style="background-color: #fdf3f3; padding: 15px; margin: 20px 0; 
                        border: 2px solid #e74c3c; border-radius: 5px; color: #c0392b;">
                Trạng thái hiện tại: <strong>Đang bị khóa</strong>
            </div>
            
            <p>Để biết thêm thông tin chi tiết và cách khôi phục tài khoản, vui lòng liên hệ với bộ phận chăm sóc khách hàng của chúng tôi:</p>
            
            <ul>
                <li>📞 Hotline: <strong>0847 775 585 (Thuận miền Tây)</strong></li>
            </ul>
            
            <p><em>Lưu ý:</em> Sau khi xác minh và xử lý, tài khoản của bạn có thể được khôi phục.</p>
            <hr style="margin: 20px 0;">
            <p>Trân trọng,<br>Đội ngũ Opulentia</p>
        </div>
        """;

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("huyen.ngocharveynash@gmail.com");
            helper.setTo(to);
            helper.setSubject("🔒 Thông báo: Tài khoản của bạn đã bị khóa");
            helper.setText(content, true); // HTML content

            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
    @Async
    public void sendAccountOpenEmail(String to) {
        try {
            String content = """
        <div style="font-family: Arial, sans-serif; padding: 20px; color: #333;">
            <h2 style="color: #27ae60;">✅ Tài khoản của bạn đã được mở khóa</h2>
            <p>Chào bạn,</p>
            <p>Chúng tôi xin thông báo rằng tài khoản của bạn trên <strong>Opulentia</strong> đã được <strong>mở khóa</strong> và bạn có thể đăng nhập, sử dụng dịch vụ bình thường.</p>
            
            <div style="background-color: #eafaf1; padding: 15px; margin: 20px 0; 
                        border: 2px solid #27ae60; border-radius: 5px; color: #2e7d32;">
                Trạng thái hiện tại: <strong>Hoạt động bình thường</strong>
            </div>
            
            <p>Nếu bạn gặp bất kỳ vấn đề nào khi đăng nhập hoặc sử dụng dịch vụ, vui lòng liên hệ bộ phận chăm sóc khách hàng của chúng tôi:</p>
            
            <ul>
                <li>📞 Hotline: <strong>0847 775 585 (Thuận miền Tây)</strong></li>
            </ul>
            
            <p>Chúc bạn có trải nghiệm mua sắm tuyệt vời tại <strong>Opulentia</strong>!</p>
            <hr style="margin: 20px 0;">
            <p>Trân trọng,<br>Đội ngũ Opulentia</p>
        </div>
        """;

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("huyen.ngocharveynash@gmail.com");
            helper.setTo(to);
            helper.setSubject("✅ Thông báo: Tài khoản của bạn đã được mở khóa");
            helper.setText(content, true); // HTML content

            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
    @Async
    public void sendDeleteAccountEmail(String to) {
        try {
            String content = """
        <div style="font-family: Arial, sans-serif; padding: 20px; color: #333;">
            <h2 style="color: #e74c3c;">❌ Thông báo: Tài khoản của bạn đã bị xóa</h2>
            <p>Chào bạn,</p>
            <p>Chúng tôi xin thông báo rằng tài khoản của bạn trên <strong>Opulentia</strong> đã bị <strong>xóa</strong> 
            do vi phạm chính sách hoặc quá lâu không hoạt động.</p>
            
            <div style="background-color: #fdecea; padding: 15px; margin: 20px 0; 
                        border: 2px solid #e74c3c; border-radius: 5px; color: #c0392b;">
                Trạng thái hiện tại: <strong>Không còn hoạt động</strong>
            </div>
            
            <p>Nếu bạn có bất kỳ thắc mắc hoặc khiếu nại, vui lòng liên hệ bộ phận chăm sóc khách hàng của chúng tôi:</p>
            <ul>
                <li>📞 Hotline: <strong>0847 775 585 (Thuận miền Tây)</strong></li>
            </ul>

            <p>Bạn có thể đăng ký tài khoản mới để tiếp tục là thành viên của <strong>Opulentia</strong>. 
            Về mức rank trước đây, bạn có thể trao đổi trực tiếp qua số hotline trên để được hỗ trợ.</p>
            
            <p>Cảm ơn bạn đã đồng hành cùng <strong>Opulentia</strong>!</p>
            <hr style="margin: 20px 0;">
            <p>Trân trọng,<br>Đội ngũ Opulentia</p>
        </div>
        """;

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("huyen.ngocharveynash@gmail.com");
            helper.setTo(to);
            helper.setSubject("❌ Thông báo: Tài khoản của bạn đã bị xóa");
            helper.setText(content, true); // HTML content

            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

}

