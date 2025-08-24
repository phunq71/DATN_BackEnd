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

@RestController
public class CallBackResstController {

    private final OrderRepository orderRepository;
    private final TransactionRepository transactionRepository;

    public CallBackResstController(OrderRepository orderRepository, TransactionRepository transactionRepository) {
        this.orderRepository = orderRepository;
        this.transactionRepository = transactionRepository;
    }

    @PostMapping("/checkout/success")
    public Boolean postForSePay(@RequestBody TransactionDTO temp, Model model) {

        String maDH = temp.getCode();


        Order order = orderRepository.findById(Integer.valueOf(maDH)).orElse(null);
        order.setStatus("ChuanBiDon");
        orderRepository.save(order);

        Transaction transaction = order.getTransaction();
        transaction.setStatus("DaThanhToan");

        transactionRepository.save(transaction);

        return true;

    }
}
