package com.main.serviceImpl;

import com.main.entity.Transaction;
import com.main.repository.TransactionRepository;
import com.main.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;

    @Override
    public void updateStatusByOrderId(Integer orderId) {
        Transaction transaction = new Transaction();
        transaction = transactionRepository.findByOrder_OrderID(orderId);
        if (transaction.getPaymentMethod().equalsIgnoreCase("cod")) {
            transaction.setTransactionDate(LocalDateTime.now());
            transaction.setStatus("DaThanhToan");
        }
        transactionRepository.save(transaction);
    }
}
