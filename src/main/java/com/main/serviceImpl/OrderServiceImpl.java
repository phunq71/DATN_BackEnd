package com.main.serviceImpl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.main.dto.*;


import com.main.entity.*;
import com.main.mapper.CustomerMapper;
import com.main.repository.*;

import com.main.service.FacilityService;
import com.main.service.OrderService;
import com.main.service.ReviewService;

import com.main.utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;



@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ReviewService reviewService;
    private final ReviewRepository reviewRepository;
    private final CustomerMapper customerMapper;
    private final CustomerRepository customerRepository;
    private final FacilityService facilityService;


    private final RestTemplate restTemplate = new RestTemplate();
    private final VoucherRepository voucherRepository;
    private final FacilityRepository facilityRepository;
    private final ItemRepository itemRepository;
    private final PromotionRepository promotionRepository;
    private final PromotionProductRepository promotionProductRepository;
    private final TransactionRepository transactionRepository;
    private final UsedVoucherRepository usedVoucherRepository;
    private final InventoryRepository inventoryRepository;
    private final CartRepository cartRepository;

    @Value("${ghn.token}")
    private String ghnToken;

    @Value("${ghn.shop-id}")
    private String shopId;


    @Override
    public List<OrderDTO> getOrdersByCustomerIdAndStatus(String customerId, String status, Integer year) {
        List<OrderDTO> orders = new ArrayList<>();
        List<OrderPriceDTO> orderPrices;

        if ("ChoLayHang".equals(status)) {
            orderPrices = new ArrayList<>();
            List<String> statusList = List.of("ChuanBiDon", "SanSangGiao", "DaYeuCauHuy");
            for (String s : statusList) {
                orders.addAll(orderRepository.getOrdersByCustomerIdAndStatus(customerId, s, year));
                orderPrices.addAll(getOrderPricesByCustomer(customerId, s, year));
            }
        } else {
            orders = orderRepository.getOrdersByCustomerIdAndStatus(customerId, status, year);

            orderPrices= getOrderPricesByCustomer(customerId, status, year);
        }



        orderPrices.forEach(System.err::println);
        orders.forEach(order -> {

            OrderPriceDTO orderPriceDTO = orderPrices.stream()
                    .filter(orderPrice -> orderPrice.getOrderId().equals(order.getOrderID()))
                    .findFirst()
                    .orElse(null);
            order.setItems(orderRepository.getOrderItemsByOrderId(order.getOrderID()));
            if (orderPriceDTO != null) {
                order.setTotalPrice(transactionRepository.findByOrder_OrderID(order.getOrderID()).getAmount());
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
        finalPrice = transactionRepository.findByOrder_OrderID(orderIdResult).getAmount();

        // Log các giá trị
        System.out.println("OrderID: " + orderIdResult);
        System.out.println("TotalPrice: " + totalPrice);
        System.out.println("ProductDiscount: " + productDiscount);
        System.out.println("VoucherDiscount: " + voucherDiscount);
        System.out.println("FinalPrice: " + finalPrice);

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
            System.out.println("OrderID: 😎😎😎😎😎😎😎😎😎😎" + orderId);
            BigDecimal totalPrice = new BigDecimal(transactionRepository.findByOrder_OrderID(orderId).getAmount().toString());
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
        System.err.println("CCCCCC: "+orderDetailDTO);
        OrderPriceDTO priceDTO= getOrderPrice(orderId);
        orderDetailDTO.setFinalPrice(priceDTO.getFinalPrice());
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
    public Map<String, Object> getOrderPreviewData( List<OrderPreviewDTO> items ) {
        Map<String, Object> orderPreviewData = new HashMap<>();
        Customer customer = customerRepository.findByCustomerId(AuthUtil.getAccountID());
        InFoCustomerOrderDTO infoCusDTO = customerMapper.toInFoCustomerOrderDTO(customer);
        List<FacilityOrderDTO> listFa = facilityService.getAllFacilities(items);
        orderPreviewData.put("customer", infoCusDTO);
        orderPreviewData.put("facilities", listFa);
        return orderPreviewData;
    }

    @Override
    public List<OrderDTO> getOrdersByCustomerIdAndOrderID(String customerId, String orderID) {
        List<OrderDTO> orders = orderRepository.getOrdersByCustomerIdAndOrderID(customerId, orderID);
        orders.forEach(order -> {
            order.setItems(orderRepository.getOrderItemsByOrderId(order.getOrderID()));
            OrderPriceDTO priceDTO = getOrderPrice(order.getOrderID());
            order.setTotalPrice(priceDTO.getFinalPrice().add(order.getShippingCosts()));
        });
        return orders;
    }


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
    public Map<String, Object> getOrderStatus(String orderCode) {
        String url = "https://dev-online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/detail";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Token", ghnToken);
        headers.set("ShopId", shopId);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("order_code", orderCode);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>() {}
        );

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, Object> responseBody = response.getBody();
            Object dataObj = responseBody.get("data");

            if (dataObj instanceof Map) {
                Map<String, Object> data = (Map<String, Object>) dataObj;
                String status = (String) data.get("status");

                List<Map<String, Object>> logs = (List<Map<String, Object>>) data.get("log");

                // Lịch sử tracking
                List<Map<String, String>> history = new ArrayList<>();
                String updatedDate = null;

                if (logs != null) {
                    for (Map<String, Object> logEntry : logs) {
                        String s = (String) logEntry.get("status");
                        String t = (String) logEntry.get("updated_date");

                        if (s != null && t != null) {
                            Map<String, String> entry = new HashMap<>();
                            entry.put("status", s);
                            entry.put("updatedTime", t);
                            history.add(entry);
                        }

                        // Lấy updated_date của trạng thái hiện tại
                        if (status.equals(logEntry.get("status"))) {
                            updatedDate = (String) logEntry.get("updated_date");
                        }
                    }
                }

                Map<String, Object> result = new HashMap<>();
                result.put("shippingStatus", status);           // trạng thái hiện tại
                result.put("updatedTime", updatedDate);         // thời gian cập nhật trạng thái hiện tại
                result.put("trackingHistory", history);         // lịch sử

                return result;
            }
        }

        throw new RuntimeException("GHN API failed: " + response.getStatusCode());
    }



    @Override
    public Order findOrderByID(Integer orderDetailID) {
        return orderRepository.findByOrderID(orderDetailID);
    }

    @Override
    public Boolean saveOrders(List<OrderDTO> orders) {
        try {
            List<Order> orderList = new ArrayList<>();
            orders.forEach(order -> {
                Order saveOrder = orderRepository.findByOrderID(order.getOrderID());
                saveOrder.setStatus(order.getStatus());
                saveOrder.setUpdateStatusAt(LocalDateTime.now());
                orderList.add(saveOrder);
            });
            orderRepository.saveAll(orderList);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public Boolean addOrderCustomer(Map<String, Object> checkoutInfo) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            // 1. Customer
            InFoCustomerOrderDTO customer = mapper.convertValue(checkoutInfo.get("customer"), InFoCustomerOrderDTO.class);

            // 2. List sản phẩm
            List<OrderPreviewDTO> listItems = mapper.convertValue(
                    checkoutInfo.get("listItems"),
                    new TypeReference<List<OrderPreviewDTO>>() {}
            );

            // 3. Các trường còn lại
            List<String> facilities = (List<String>) checkoutInfo.get("facilities");
            String facilityId = (String) checkoutInfo.get("facilityId");
            String lastTime = (String) checkoutInfo.get("lastTime");
            String paymentMethod = (String) checkoutInfo.get("paymentMethod");
            String voucherId = (String) checkoutInfo.get("voucherId");
            Boolean type = checkoutInfo.get("type") != null ? (Boolean) checkoutInfo.get("type") : null;
            String hauMai1 = (String) checkoutInfo.get("hauMai1");
            String hauMai2 = (String) checkoutInfo.get("hauMai2");

            BigDecimal discountTotal = new BigDecimal(checkoutInfo.get("discountTotal").toString());
            BigDecimal totalAmount = new BigDecimal(checkoutInfo.get("totalAmount").toString());

            // Thêm vào Order và OrderDetails
            Order order = new Order();
            order.setOrderDate(LocalDateTime.now());
            order.setStatus("ChoXacNhan");
            order.setShippingAddress(customer.getCustomerAddress());
            order.setNote(customer.getNote());
            order.setIsOnline(true);
            order.setShipMethod("GHN");
            order.setCostShip(customer.getCostShip());
            order.setCustomer(customerRepository.findByCustomerId(customer.getCustomerId()));
            order.setStaff(null);
            System.out.println("✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅ : " + voucherId);
            if (voucherId != null && !voucherId.equals(""))
                order.setVoucher(voucherRepository.findById(voucherId).get());

            order.setFacility(facilityRepository.findById(facilityId).get());
            order.setUpdateStatusAt(LocalDateTime.now());
            order.setAddressIdGHN(customer.getCustomerAddress());
            order.setDiscountCost(customer.getDiscountCost());
            order.setShippingCode(null);

            order = orderRepository.save(order);

            Order finalOrder = order;
            List<OrderDetail> orderDetails = new ArrayList<>();
            listItems.forEach(item -> {
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrder(finalOrder);
                orderDetail.setItem(itemRepository.findById(item.getItem_id()).get());
                orderDetail.setQuantity(item.getQuantity());
                orderDetail.setUnitPrice(item.getPrice());
                orderDetail.setTotalPrice(item.getTotal_price());
                orderDetail.setPromotionProduct(promotionProductRepository.findById(item.getPPID()).get());
                orderDetails.add(orderDetail);
            });

            orderDetailRepository.saveAll(orderDetails);

            // Tạo phiếu thu
            Transaction transaction = new Transaction();
            transaction.setTransactionType(true);
            BigDecimal amount =
                    (totalAmount != null ? totalAmount : BigDecimal.ZERO)
                            .subtract(discountTotal != null ? discountTotal : BigDecimal.ZERO)
                            .add(customer != null && customer.getDiscountCost() != null
                                    ? customer.getDiscountCost()
                                    : BigDecimal.ZERO);

            transaction.setAmount(amount);
            transaction.setTransactionDate(LocalDateTime.now());
            transaction.setPaymentMethod(paymentMethod);
            transaction.setDescription("Người dùng trã tiền mua hàng");
            transaction.setStatus("ChuaThanhToan");
            transaction.setStaff(null); transaction.setApprover(null); transaction.setReturnRequest(null);
            transaction.setOrder(order);
            transaction.setFacility(facilityRepository.findById(facilityId).get());

            transactionRepository.save(transaction);



                // Trừ voucher đã xử dụng
            if (voucherId != null && !voucherId.equals("")) {
                if (type != null && type == true) {
                    Voucher voucher = voucherRepository.findById(voucherId).orElseThrow();
                    Customer cus = customerRepository.findById(customer.getCustomerId()).orElseThrow();

                    UsedVoucherID usedVoucherID = new UsedVoucherID();
                    usedVoucherID.setVoucher(voucher.getVoucherID());  // String ID
                    usedVoucherID.setCustomer(cus.getCustomerId());    // String ID

                    UsedVoucher usedVoucher = new UsedVoucher();
                    usedVoucher.setVoucher(voucher);        // đã fetch từ DB
                    usedVoucher.setCustomer(cus);      // đã fetch từ DB
                    usedVoucher.setType(true);
                    usedVoucher.setUsedVoucherID(new UsedVoucherID(
                            voucher.getVoucherID(),
                            customer.getCustomerId()
                    ));
                    usedVoucherRepository.save(usedVoucher);


                } else if (type != null) {
                    UsedVoucher usedVoucher = new UsedVoucher();
                    Voucher voucher = voucherRepository.findById(voucherId).get();
                    Customer cus = customerRepository.findById(customer.getCustomerId()).get();

                    usedVoucher = usedVoucherRepository.findByVoucherAndCustomer(voucher, cus);
                    usedVoucher.setType(true);
                    usedVoucherRepository.save(usedVoucher);
                }
            }

            // Trừ trong kho và xóa cart
            listItems.forEach(item -> {
               InventoryId inventoryId = new InventoryId(item.getItem_id(), facilityId);
               Inventory inventory = inventoryRepository.getInventoryById(inventoryId);
               inventory.setQuantity(inventory.getQuantity() - item.getQuantity());
               inventoryRepository.save(inventory);

               Cart cart = cartRepository.getCartByItem_itemIdAndCustomer_customerId(item.getItem_id(), customer.getCustomerId());
               cartRepository.delete(cart);
            });


            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}
