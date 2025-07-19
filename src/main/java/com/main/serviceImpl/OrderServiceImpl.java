package com.main.serviceImpl;

import com.main.dto.*;
import com.main.entity.OrderDetail;
import com.main.repository.OrderDetailRepository;
import com.main.repository.OrderRepository;
import com.main.repository.ReviewRepository;
import com.main.service.OrderService;
import com.main.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ReviewService reviewService;
    private final ReviewRepository reviewRepository;

    public OrderServiceImpl(OrderRepository orderRepository, OrderDetailRepository orderDetailRepository, ReviewService reviewService, ReviewRepository reviewRepository) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.reviewService = reviewService;
        this.reviewRepository = reviewRepository;
    }

    @Override
    public List<OrderDTO> getOrdersByCustomerIdAndStatus(String customerId, String status, Integer year) {
        List<OrderDTO> orders = new ArrayList<>();

        if ("ChoLayHang".equals(status)) {
            List<String> statusList = List.of("ChuanBiDon", "SanSangGiao", "DaYeuCauHuy");
            for (String s : statusList) {
                orders.addAll(orderRepository.getOrdersByCustomerIdAndStatus(customerId, s, year));
            }
        } else {
            orders = orderRepository.getOrdersByCustomerIdAndStatus(customerId, status, year);
        }

        List<OrderPriceDTO> orderPrices = getOrderPricesByCustomer(customerId, status, year);

        orders.forEach(order -> {
            OrderPriceDTO orderPriceDTO = orderPrices.stream()
                    .filter(orderPrice -> orderPrice.getOrderId().equals(order.getOrderID()))
                    .findFirst()
                    .orElse(null);
            order.setItems(orderRepository.getOrderItemsByOrderId(order.getOrderID()));
            if (orderPriceDTO != null) {
                order.setTotalPrice(orderPriceDTO.getFinalPrice().add(order.getShippingCosts()));
            } else {
                System.err.println("Không tìm thấy OrderPriceDTO cho OrderID: " + order.getOrderID());
            }
        });

        return orders;
    }


    @Override
    public OrderPriceDTO getOrderPrice(Integer orderId) {
        Object[] orderPrices = orderRepository.getOrderPrices(orderId);

        if (orderPrices == null || orderPrices.length == 0) {
            throw new RuntimeException("Không tìm thấy đơn hàng");
        }

        // Xử lý trường hợp kết quả bị nested (nếu có)
        Object[] resultArray = orderPrices;
        if (orderPrices.length == 1 && orderPrices[0] instanceof Object[]) {
            resultArray = (Object[]) orderPrices[0];
        }

        if (resultArray.length < 5) {
            throw new RuntimeException("Dữ liệu trả về không đủ 5 cột");
        }

        // Ép kiểu về BigDecimal
        Integer orderIdResult = (Integer) resultArray[0];
        BigDecimal totalPrice = convertToBigDecimal(resultArray[1]);
        BigDecimal productDiscount = convertToBigDecimal(resultArray[2]);
        BigDecimal voucherDiscount = convertToBigDecimal(resultArray[3]);
        BigDecimal finalPrice = convertToBigDecimal(resultArray[4]);

//        // Log các giá trị
//        System.out.println("OrderID: " + orderIdResult);
//        System.out.println("TotalPrice: " + totalPrice);
//        System.out.println("ProductDiscount: " + productDiscount);
//        System.out.println("VoucherDiscount: " + voucherDiscount);
//        System.out.println("FinalPrice: " + finalPrice);

        return new OrderPriceDTO(
                orderIdResult,
                totalPrice,
                productDiscount,
                voucherDiscount,
                finalPrice
        );
    }

    // Hàm hỗ trợ chuyển đổi sang BigDecimal
    private BigDecimal convertToBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof Number) {
            return new BigDecimal(value.toString());
        }
        try {
            return new BigDecimal(value.toString());
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    @Override
    public List<OrderPriceDTO> getOrderPricesByCustomer(String customerId, String status, Integer year) {
        List<OrderPriceDTO> orderPriceDTOs = new ArrayList<>();
        List<Object[]> orderPrices = orderRepository.getOrderPricesByCustomer(customerId, status ,year);
        orderPrices.forEach(orderPrice -> {
            Integer orderId = (Integer) orderPrice[0];
            BigDecimal totalPrice = new BigDecimal(orderPrice[1].toString());
            BigDecimal productDiscount = new BigDecimal(orderPrice[2].toString());
            BigDecimal voucherDiscount = new BigDecimal(orderPrice[3].toString());
            BigDecimal finalPrice = new BigDecimal(orderPrice[4].toString());
            orderPriceDTOs.add(new OrderPriceDTO(orderId, totalPrice, productDiscount, voucherDiscount, finalPrice));
        });

        return orderPriceDTOs;
    }

    @Override
    public List<OrderItemDTO> getOrderItemsByOrderId(Integer orderId) {

        return orderRepository.getOrderItemsByOrderId(orderId);
    }

    @Override
    public boolean existsByOrderIDAndCustomer_CustomerId(Integer orderID, String customerCustomerId) {
        return orderRepository.existsByOrderIDAndCustomer_CustomerId(orderID, customerCustomerId);
    }

    @Override
    public OrderDetailDTO getOrderDetailByOrderID(Integer orderId) {
        OrderDetailDTO orderDetailDTO = orderRepository.getOrderDetailByOrderId(orderId);
        OrderPriceDTO priceDTO= getOrderPrice(orderId);
        orderDetailDTO.setFinalPrice(priceDTO.getFinalPrice().add(orderDetailDTO.getShippingCost()));
        orderDetailDTO.setDiscountVoucherPrice(priceDTO.getVoucherDiscount());
        orderDetailDTO.setDiscountProductPrice(priceDTO.getProductDiscount());

        List<OrderItemDTO> orderItemDTOS = orderRepository.getOrderItemsByOrderId(orderId);

        orderDetailDTO.setItems(orderItemDTOS);

        return orderDetailDTO;
    }

    @Override
    public List<Integer> getOrderYearByCustomerId(String customerId) {
        return orderRepository.getOrderYearByCustomerId(customerId);
    }

    @Override
    public List<OrderDTO> getOrdersByKeyword(String customerId, String keyword) {
        List<OrderDTO> orders = orderRepository.getOrdersByCustomerIdAndKeywords(customerId, keyword);

        orders.forEach(order -> {
            order.setItems(orderRepository.getOrderItemsByOrderId(order.getOrderID()));
            OrderPriceDTO priceDTO = getOrderPrice(order.getOrderID());
            order.setTotalPrice(priceDTO.getFinalPrice().add(order.getShippingCosts()));
        });


        return orders;
    }

    @Override
    public Boolean checkOrderDetailByCustomerIDAndODID(String customerId, Integer orderDetailID) {
        Optional<OrderDetail> orderDetailOptional= orderDetailRepository.findById(orderDetailID);
        if(orderDetailOptional.isPresent()) {
            OrderDetail orderDetail = orderDetailOptional.get();
            return orderDetail.getOrder().getCustomer().getCustomerId().equals(customerId);
        }

        return false;
    }



    /**
     * Xử lý quyền truy cập và trạng thái đánh giá cho 1 OrderDetail cụ thể.
     * <p>
     * Luật xử lý:
     * - Chỉ cho phép nếu OrderDetail thuộc về Customer đang đăng nhập và đơn hàng đã giao ("DaGiao").
     * - Trong vòng 15 ngày tính từ updateStatusAt:
     *      + Nếu chưa có đánh giá: trả về status = "NEW"
     *      + Nếu đã có đánh giá: trả về status = "EDIT"
     * - Quá 15 ngày:
     *      + Nếu đã có đánh giá: trả về status = "VIEW"
     *      + Nếu chưa có đánh giá: trả về status = "EXPIRED" (FORBIDDEN)
     * - Nếu không tìm thấy OrderDetail: trả về status = "NOT_FOUND"
     * - Nếu đơn hàng chưa giao xong: trả về status = "INVALID_STATUS" (FORBIDDEN)
     *
     * @param orderDetailID ID của chi tiết đơn hàng (OrderDetail) cần kiểm tra.
     * @param customerId ID của customer đang đăng nhập.
     * @return ResponseEntity chứa JSON:
     * {
     *     "status": "NEW" | "EDIT" | "VIEW" | "EXPIRED" | "INVALID_STATUS" | "NOT_FOUND",
     *     "data": ReviewDTO hoặc "" nếu chưa có
     * }
     *
     * Status ý nghĩa:
     * - "NEW" : Được phép tạo đánh giá mới
     * - "EDIT" : Được phép chỉnh sửa đánh giá
     * - "VIEW" : Chỉ được xem đánh giá (quá hạn chỉnh sửa)
     * - "EXPIRED" : Đã hết hạn, không được tạo đánh giá nữa
     * - "INVALID_STATUS" : Đơn hàng chưa được giao xong
     * - "NOT_FOUND" : Không tìm thấy OrderDetail
     */
    @Override
    public ResponseEntity<?> handleReviewAccess(Integer orderDetailID, String customerId) {

        Optional<OrderDetail> odOpt = orderDetailRepository.findById(orderDetailID);
        if (odOpt.isEmpty()) {
            return new ResponseEntity<>(Map.of(
                    "status", "NOT_FOUND",
                    "data", ""
            ), HttpStatus.NOT_FOUND);
        }

        OrderDetail od = odOpt.get();

        // Kiểm tra trạng thái đơn hàng phải là "DaGiao"
        if (!"DaGiao".equalsIgnoreCase(od.getOrder().getStatus())) {
            return new ResponseEntity<>(Map.of(
                    "status", "INVALID_STATUS",
                    "data", ""
            ), HttpStatus.FORBIDDEN);
        }


        LocalDateTime updateStatusAt = od.getOrder().getUpdateStatusAt();
        LocalDateTime today = LocalDateTime.now();
        long daysBetween = ChronoUnit.DAYS.between(updateStatusAt, today);

        ReviewDTO reviewDTO = reviewService.getReviewByCustomerIDAndODID(customerId, orderDetailID);

        if (daysBetween <= 15) {
            if (reviewDTO == null) {
                return ResponseEntity.ok(Map.of(
                        "status", "NEW",
                        "data", reviewRepository.getReviewItemByItemID(od.getItem().getItemId())
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                        "status", "EDIT",
                        "data", reviewDTO
                ));
            }
        } else {
            if (reviewDTO == null) {
                return new ResponseEntity<>(Map.of(
                        "status", "EXPIRED",
                        "data", ""
                ), HttpStatus.FORBIDDEN);
            } else {
                return ResponseEntity.ok(Map.of(
                        "status", "VIEW",
                        "data", reviewDTO
                ));
            }
        }
    }

}
