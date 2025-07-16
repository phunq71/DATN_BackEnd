package com.main.serviceImpl;

import com.main.dto.OrderDTO;
import com.main.dto.OrderDetailDTO;
import com.main.dto.OrderItemDTO;
import com.main.dto.OrderPriceDTO;
import com.main.repository.OrderRepository;
import com.main.service.OrderService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    public final OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public List<OrderDTO> getOrdersByCustomerIdAndStatus(String customerId, String status) {
        List<OrderDTO> orders = new ArrayList<>();

        if ("ChoLayHang".equals(status)) {
            List<String> statusList = List.of("ChuanBiDon", "SanSangGiao", "DaYeuCauHuy");
            for (String s : statusList) {
                orders.addAll(orderRepository.getOrdersByCustomerIdAndStatus(customerId, s));
            }
        } else {
            orders = orderRepository.getOrdersByCustomerIdAndStatus(customerId, status);
        }

        List<OrderPriceDTO> orderPrices = getOrderPricesByCustomer(customerId);

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
    public List<OrderPriceDTO> getOrderPricesByCustomer(String customerId) {
        List<OrderPriceDTO> orderPriceDTOs = new ArrayList<>();
        List<Object[]> orderPrices = orderRepository.getOrderPricesByCustomer(customerId);
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
}
