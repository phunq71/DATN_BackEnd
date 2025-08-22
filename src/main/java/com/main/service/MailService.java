package com.main.service;

import com.main.entity.Customer;
import com.main.entity.Voucher;
import com.main.repository.VoucherRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
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
    private final VoucherRepository voucherRepository;


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

            helper.setFrom("ngoquocphu2005@gmail.com");
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
            Voucher voucher = new Voucher();
            voucher = voucherRepository.findById(voucherCode).get();
            Integer sl = voucher.getQuantityRemaining();

            String discountDisplay = discountDetail < 100
                    ? discountDetail + "%"
                    : String.format("%,d VNĐ", discountDetail); // thêm dấu phẩy ngăn cách nghìn

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
        <p><strong>Số lượt dùng giới hạn còn:</strong> %s</p>
        
        
        <p>Hãy nhanh tay sử dụng mã này trước khi hết hạn/ hết lượt nhé nhé!</p>
        <hr style="margin: 20px 0;">
        <p>Trân trọng,<br>Đội ngũ Opulentia</p>
    </div>
""".formatted(
                    voucherCode,
                    discountDisplay,
                    expiryDate != null ? expiryDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : " ",
                    sl
            );



            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("ngoquocphu2005@gmail.com");
            helper.setTo(to);
            helper.setSubject("🎁 Nhận ngay voucher ưu đãi từ Opulentia!");
            helper.setText(content, true); // HTML content

            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Async
    public void sendCongratulationRank(Customer customer) {
        try {
            String rankName = customer.getMembership().getRank();

            String content = """
            <div style="font-family: Arial, sans-serif; padding: 20px; color: #333;">
                <h2 style="color: #27ae60;">🎉 Chúc mừng %s!</h2>
                <p>Xin chào <strong>%s</strong>,</p>
                <p>Bạn vừa đạt được hạng thành viên mới: 
                   <span style="font-size: 20px; font-weight: bold; color: #e67e22;">
                       %s
                   </span>
                </p>
                
                <p>Hãy tận hưởng những đặc quyền và ưu đãi hấp dẫn dành riêng cho hạng của bạn nhé!</p>
                
                <div style="margin: 20px 0; padding: 15px; background: #f1f1f1; border-left: 5px solid #27ae60;">
                    <p style="margin: 0;">💎 <strong>Quyền lợi hạng %s</strong> sẽ được tự động áp dụng cho tài khoản của bạn.</p>
                </div>
                
                <p>Chúc bạn có trải nghiệm mua sắm tuyệt vời cùng <strong>Opulentia</strong>.</p>
                
                <hr style="margin: 20px 0;">
                <p>Trân trọng,</p>
                <p><strong>Đội ngũ Opulentia</strong></p>
            </div>
            """.formatted(customer.getFullName(), customer.getFullName(), rankName, rankName);

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("ngoquocphu2005@gmail.com");
            helper.setTo(customer.getAccount().getEmail());
            helper.setSubject("🎉 Chúc mừng bạn đã trở thành " + rankName + "!");
            helper.setText(content, true);

            javaMailSender.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Async
    public void sendRejectOrderEmail(String orderId, String reason, String customerEmail, String customerFullName) {
        try {
            String content = """
            <div style="font-family: Arial, sans-serif; padding: 20px; color: #333;">
                <h2>Đơn hàng %s đã bị từ chối</h2>
                <p>Xin chào <strong>%s</strong>,</p>
                <p>Lý do từ chối: <strong>%s</strong></p>
                <p>Vui lòng liên hệ bộ phận CSKH để biết thêm chi tiết.</p>
                <hr>
                <p>Trân trọng,</p>
                <p><strong>Đội ngũ Opulentia</strong></p>
            </div>
        """.formatted(orderId, customerFullName, reason);

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("ngoquocphu2005@gmail.com");
            helper.setTo(customerEmail);
            helper.setSubject("Đơn hàng " + orderId + " đã bị từ chối");
            helper.setText(content, true);

            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }




}

