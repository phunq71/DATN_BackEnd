package com.main.serviceImpl;

import com.main.dto.ReturnItemDTO;
import com.main.dto.ReturnItemFormDTO;
import com.main.entity.*;
import com.main.repository.OrderRepository;
import com.main.repository.ReturnItemRepository;
import com.main.repository.ReturnRequestRepository;
import com.main.repository.ReviewImageRepository;
import com.main.service.ReturnItemService;
import com.main.utils.FileUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReturnItemServiceImpl implements ReturnItemService {
    private final ReturnRequestRepository returnRequestRepository;
    private final ReturnItemRepository returnItemRepository;
    private final OrderRepository orderRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final FileUtil fileUtil;

    public ReturnItemServiceImpl(ReturnRequestRepository returnRequestRepository, ReturnItemRepository returnItemRepository, OrderRepository orderRepository, ReviewImageRepository reviewImageRepository, FileUtil fileUtil) {
        this.returnRequestRepository = returnRequestRepository;
        this.returnItemRepository = returnItemRepository;
        this.orderRepository = orderRepository;
        this.reviewImageRepository = reviewImageRepository;
        this.fileUtil = fileUtil;
    }

    @Override
    public List<ReturnItemDTO> getReturnItemByOrderID(int orderID) {
        if(returnRequestRepository.existsByOrder_OrderID(orderID)) {
            return new ArrayList<>();
        }

        return returnItemRepository.getReturnItemByOrderID(orderID);
    }

    @Override
    public boolean checkOrderAndCustomer(int orderID, String customerID) {
        Optional<Order> optOrder = orderRepository.findByOrderIDAndCustomer_CustomerId(orderID, customerID);
        return optOrder.map(order -> order.getStatus().equals("DaGiao") && order.getUpdateStatusAt().isAfter(LocalDateTime.now().minusDays(7)))
                .orElse(false);
    }

    @Override
    public boolean saveReturnItem(int orderID, List<ReturnItemFormDTO> items, String customerID, List<MultipartFile> savedFiles) {
        if(returnRequestRepository.existsByOrder_OrderID(orderID)) {
            return false;
        }

        try {
            Order order = new Order(orderID);
            Customer customer = new Customer(customerID);

            ReturnRequest returnRequest = new ReturnRequest();
            returnRequest.setOrder(order);
            returnRequest.setRequestDate(LocalDateTime.now());
            returnRequest.setIsOnline(true);
            returnRequest.setStatus("NEW");
            ReturnRequest savedReturnRequest = returnRequestRepository.save(returnRequest);

            // Gom tất cả ReturnItem vô 1 list
            List<ReturnItem> returnItems = new ArrayList<>();
            List<ReviewImage> reviewImages = new ArrayList<>();

            items.forEach(item -> {
                ReturnItem returnItem = new ReturnItem();
                returnItem.setReturnRequest(savedReturnRequest);
                returnItem.setQuantity(item.getQuantity());
                returnItem.setReason(item.getReason());
                returnItem.setOrderDetail(new OrderDetail(item.getOrderDetailID()));
                returnItems.add(returnItem);
            });

            // Lưu ReturnItem 1 lần cho nhanh
            List<ReturnItem> savedReturnItems = returnItemRepository.saveAll(returnItems);

            // Gom ReviewImage của từng item
            for (int i = 0; i < items.size(); i++) {
                ReturnItem savedReturnItem = savedReturnItems.get(i);
                ReturnItemFormDTO dto = items.get(i);
                dto.getFileNames().forEach(fileName -> {
                    ReviewImage reviewImage = new ReviewImage(savedReturnItem.getReturnItemId(), fileName);
                    reviewImages.add(reviewImage);
                });
            }

            // Lưu ReviewImage 1 lần luôn
            reviewImageRepository.saveAll(reviewImages);

            // Lưu file xuống ổ cứng từng cái
            savedFiles.forEach(file -> {
                try {
                    fileUtil.saveImage2(file);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            return true;

        } catch (Exception e) {
            System.err.println(e.getMessage());
            return false;
        }
    }


//    @Override
//    public boolean checkOrderAndCustomer(int orderID, String customerID) {
//        System.err.println("👉 Gọi hàm checkOrderAndCustomer với orderID = " + orderID + ", customerID = " + customerID);
//
//        Optional<Order> optOrder = orderRepository.findByOrderIDAndCustomer_CustomerId(orderID, customerID);
//        System.err.println("👉 Tìm Order: " + (optOrder.isPresent() ? "Tìm thấy" : "Không tìm thấy"));
//
//        if (optOrder.isPresent()) {
//            LocalDateTime orderDate = optOrder.get().getUpdateStatusAt();
//            System.err.println("👉 Ngày nhận hàng: " + orderDate);
//
//            LocalDateTime now = LocalDateTime.now();
//            System.err.println("👉 Thời điểm hiện tại: " + now);
//            System.err.println("👉 7 ngày trước: " + now.minusDays(7));
//
//            boolean isAfter = orderDate.isAfter(now.minusDays(7));
//            System.err.println("👉 Ngày đặt hàng sau 7 ngày trước hay không: " + isAfter);
//
//            return isAfter;
//        } else {
//            System.err.println("👉 Không tìm thấy Order, trả về FALSE");
//            return false;
//        }
//    }

}
