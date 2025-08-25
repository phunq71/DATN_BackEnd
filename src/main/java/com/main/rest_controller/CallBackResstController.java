package com.main.rest_controller;

import com.main.dto.TransactionDTO;
import com.main.entity.Order;
import com.main.entity.Transaction;
import com.main.repository.OrderRepository;
import com.main.repository.TransactionRepository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
public class CallBackResstController {

    private final OrderRepository orderRepository;
    private final TransactionRepository transactionRepository;

    public CallBackResstController(OrderRepository orderRepository, TransactionRepository transactionRepository) {
        this.orderRepository = orderRepository;
        this.transactionRepository = transactionRepository;
    }

    @PostMapping("/checkout/success")
    public Boolean postForSePay(@RequestBody TransactionDTO temp) {

        // content = mã đơn hàng
        String maDH = temp.getContent();

        Order order = orderRepository.findById(Integer.valueOf(maDH)).orElse(null);
        if (order == null) {
            return false; // không tìm thấy đơn hàng
        }

        Transaction transaction = order.getTransaction();
        if (transaction == null) {
            return false; // đơn chưa có transaction
        }

        // Số tiền cần thanh toán trong đơn
        BigDecimal soTienTrongDon = transaction.getAmount();

        // Số tiền nhận từ ngân hàng
        BigDecimal soTienThanhToan = temp.getTransferAmount();

        // So sánh chính xác số tiền
        if (soTienTrongDon.compareTo(soTienThanhToan) == 0) {
            order.setStatus("ChuanBiDon");
            transaction.setStatus("DaThanhToan");
        } else {
            transaction.setStatus("ThatBai_SaiSoTien");
        }

        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setPaymentCode(temp.getReferenceCode());

        orderRepository.save(order);
        transactionRepository.save(transaction);

        return true;
    }

}
