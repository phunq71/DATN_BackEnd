package com.main.serviceImpl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;



@Slf4j
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
    private final StaffRepository staffRepository;
    private final LogOrderRepository logOrderRepository;

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
            System.out.println("Đã chạy xong 🤬🤬🤬🤬🤬🤬");
            orderPrices= getOrderPricesByCustomer(customerId, status, year);
            System.out.println("🥕🥕🥕🥕🥕🥕🥕🥕🥕🥕🥕🥕");
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
            System.out.println("🙉");
            BigDecimal productDiscount = new BigDecimal(orderPrice[2].toString());
            System.out.println("🙉 🙉");
            BigDecimal voucherDiscount = new BigDecimal(orderPrice[3].toString());
            System.out.println("🙉 🙉 🙉");
            BigDecimal finalPrice = new BigDecimal(orderPrice[4].toString());
            System.out.println("🙉 🙉 🙉 🙉");
            orderPriceDTOs.add(new OrderPriceDTO(orderId, totalPrice, productDiscount, voucherDiscount, finalPrice));
            System.out.println("🙉 🙉 🙉 🙉 🙉");
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
    //Gọi API GHN để laays trạng thái đơn hàng
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
    public Page<OrdManagement_OrderDTO> getOrders(
            LocalDateTime startDate,
            LocalDateTime endDate,
            String status,
            String facilityId,
            String parentId,
            Integer orderId,
            Pageable pageable){
        Page<OrdManagement_OrderDTO> page = orderRepository.getOrders(startDate, endDate, status, facilityId, parentId, orderId, pageable);
        page.forEach(orderDTO -> {
             List <LogOrderDTO> list = new ArrayList();
             list = logOrderRepository.findByOrderId(orderDTO.getOrderID());
             orderDTO.setLogOrders(list);
        });
        return page;
    }

    @Override
    public Page<OrdManagement_OrderDTO> getOrdersWithOrderDate(Pageable pageable, LocalDateTime orderDate, String status) {
        return orderRepository.getOrdersWithOrderDate(pageable, orderDate, status);
    }

    @Override
    public List<OrdManagement_ProductDTO> getProductsByOrderID(Integer orderID) {
        return orderRepository.getProductsByOrderID(orderID);
    }

    @Override
    public Order save(Order order) {
        return orderRepository.save(order);
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
            String lastTimeStr = (String) checkoutInfo.get("lastTime");
            LocalDate lastTime = OffsetDateTime.parse(lastTimeStr).toLocalDate();
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
            order.setAddressIdGHN(customer.getCustomerAddressIdGHN());
            order.setDiscountCost(customer.getDiscountCost());
            order.setShippingCode(null);
            order.setDelivery(lastTime);

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

                // trừ vào khuyến mãi của sản phẩm
                PromotionProduct pp = null;
                if (item.getPPID() != null) {
                    pp = promotionProductRepository.findById(item.getPPID()).orElse(null);
                    pp.setQuantityRemaining(pp.getQuantityRemaining() - orderDetail.getQuantity());
                    pp.setQuantityUsed(pp.getQuantityUsed() +  orderDetail.getQuantity());
                    promotionProductRepository.save(pp);
                }
                orderDetail.setPromotionProduct(pp);
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

                    voucher.setQuantityRemaining(voucher.getQuantityRemaining() -1);
                    voucher.setQuantityUsed(voucher.getQuantityUsed() + 1);
                    usedVoucherRepository.save(usedVoucher);


                } else if (type != null ) {
                    UsedVoucher usedVoucher = new UsedVoucher();
                    Voucher voucher = voucherRepository.findById(voucherId).get();
                    Customer cus = customerRepository.findById(customer.getCustomerId()).get();
                    System.out.println( voucher.getVoucherID() + customer.getCustomerId() +"👉👉👉👉👉" );
                    usedVoucher = usedVoucherRepository.findByVoucherAndCustomer(voucher, cus);
                    System.out.println(usedVoucher.getVoucher().getVoucherID());
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

            // lưu thêm vào log
            LogOrders logOrders = new LogOrders();
            logOrders.setContent("Khách hàng đặt hàng");
            logOrders.setUpdateAt(LocalDateTime.now());
            logOrders.setOrder(order);
            logOrderRepository.save(logOrders);


            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<OrderDetailDTO> getAllOrderIdShippingCodes() {
        return orderRepository.getAllOrderIdShippingCodes();
    }

    @Override
    public OrderDetailDTO getOrderIdByShippingCodes(String shippingCode) {
        return orderRepository.getOrderByShippingCodes(shippingCode);
    }
    //Tạo đơn GHN
    @Override
    public String createGhnOrder(GhnOrderRequestDTO payload) {
        String idStaff = AuthUtil.getAccountID();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Token", ghnToken);
            headers.set("ShopId", shopId);

            HttpEntity<GhnOrderRequestDTO> requestEntity = new HttpEntity<>(payload, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://dev-online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/create",
                    requestEntity,
                    String.class
            );
//            Sau khi đơn hàng đc tạo lấy order_code
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            String ghnOrderCode = root.path("data").path("order_code").asText();
//            ghi staff chuảẩn bị đơn
            Order order = orderRepository.findByOrderID(payload.getOrderID());
            if (order == null) {
                throw new RuntimeException("Không tìm thấy đơn hàng với ID: " + payload.getOrderID());
            }
            Staff staff = staffRepository.getByStaffID(idStaff);
            order.setStaff(staff);
            order.setStatus("SanSangGiao");
            order.setShippingCode(ghnOrderCode);
            order.setUpdateStatusAt(LocalDateTime.now());
            orderRepository.save(order);

            // thêm lưu log
            LogOrders logOrders = new LogOrders();
            logOrders.setStaff(staff);
            logOrders.setOrder(order);
            logOrders.setContent("Đóng hàng xong, đợi shipper lấy đơn");
            logOrders.setUpdateAt(LocalDateTime.now());
            logOrderRepository.save(logOrders);

            return response.getBody(); // có thể parse JSON nếu cần
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Gọi GHN thất bại: " + e.getMessage());
        }
    }

    @Override
    public Boolean cancelOrder(Integer orderId) {
        try {
            Order order = orderRepository.findById(orderId).get();
            String facilityId = order.getFacility().getFacilityId();
            // Cộng lại trong kho
            order.getOrderDetails().forEach(orderDetail -> {
                InventoryId inventoryId = new InventoryId(orderDetail.getItem().getItemId(), facilityId);
                Inventory inventory = inventoryRepository.getInventoryById(inventoryId);
                inventory.setQuantity(inventory.getQuantity() + orderDetail.getQuantity());
                inventoryRepository.save(inventory);

                // Hoàn lại khuyến mãi cho sản phẩm
                PromotionProduct pp = null;
                if (orderDetail.getPromotionProduct() != null) {
                    pp = promotionProductRepository.findById(orderDetail.getPromotionProduct().getPromotionProductID()).orElse(null);
                    pp.setQuantityRemaining(pp.getQuantityRemaining() + orderDetail.getQuantity());
                    pp.setQuantityUsed(pp.getQuantityUsed() -  orderDetail.getQuantity());
                    promotionProductRepository.save(pp);
                }
            });

            // chỉnh phiếu thu
            Transaction transaction = order.getTransaction();
            transaction.setStatus("KhongThanhToan");
            transactionRepository.save(transaction);
            // lưu log + Lưu lại thành đã hủy
            LogOrders logOrders = new LogOrders();
            logOrders.setStaff(null);
            logOrders.setOrder(order);
            logOrders.setContent("khách đã hủy đơn vì lý do cá nhân!");
            logOrders.setUpdateAt(LocalDateTime.now());
            logOrderRepository.save(logOrders);

            // Lưu đơn hàng thành đã hủy
            order.setUpdateStatusAt(LocalDateTime.now());
            order.setStatus("DaHuy");
            orderRepository.save(order);

            return true;
        }catch (Exception e) {
            return false;
        }
    }


    @Override
    public Boolean cancelOrder2(Integer orderId, String reason) {
        try {
            Order order = orderRepository.findById(orderId).get();
            String facilityId = order.getFacility().getFacilityId();
            // Cộng lại trong kho
            order.getOrderDetails().forEach(orderDetail -> {
                InventoryId inventoryId = new InventoryId(orderDetail.getItem().getItemId(), facilityId);
                Inventory inventory = inventoryRepository.getInventoryById(inventoryId);
                inventory.setQuantity(inventory.getQuantity() + orderDetail.getQuantity());
                inventoryRepository.save(inventory);

                // Hoàn lại khuyến mãi cho sản phẩm
                PromotionProduct pp = null;
                if (orderDetail.getPromotionProduct() != null) {
                    pp = promotionProductRepository.findById(orderDetail.getPromotionProduct().getPromotionProductID()).orElse(null);
                    pp.setQuantityRemaining(pp.getQuantityRemaining() + orderDetail.getQuantity());
                    pp.setQuantityUsed(pp.getQuantityUsed() -  orderDetail.getQuantity());
                    promotionProductRepository.save(pp);
                }
            });

            // chỉnh phiếu thu
            Transaction transaction = order.getTransaction();
            transaction.setStatus("KhongThanhToan");
            transactionRepository.save(transaction);
            // lưu log + Lưu lại thành đã hủy
            LogOrders logOrders = new LogOrders();
            Staff staff = staffRepository.findById(AuthUtil.getAccountID()).get();
            logOrders.setStaff( staff );
            logOrders.setOrder(order);
            logOrders.setContent("Từ chối nhận đơn, lý do: " + reason.toLowerCase(Locale.ROOT));
            logOrders.setUpdateAt(LocalDateTime.now());
            logOrderRepository.save(logOrders);

            // Lưu đơn hàng thành đã hủy
            order.setUpdateStatusAt(LocalDateTime.now());
            order.setStatus("DaHuy");
            orderRepository.save(order);

            return true;
        }catch (Exception e) {
            return false;
        }
    }


    @Override
    public Boolean cancelOrder3(Integer orderId, String reason) {
        try {
            Order order = orderRepository.findById(orderId).get();
            // lưu log + Lưu lại thành đã hủy
            LogOrders logOrders = new LogOrders();
            logOrders.setStaff(null);
            logOrders.setOrder(order);
            logOrders.setContent("Khách yêu cầu hủy đơn, lý do: " + reason.toLowerCase(Locale.ROOT));
            logOrders.setUpdateAt(LocalDateTime.now());
            logOrderRepository.save(logOrders);

            // Lưu đơn hàng thành đã hủy
            order.setUpdateStatusAt(LocalDateTime.now());
            order.setStatus("YeuCauHuy");
            orderRepository.save(order);
            System.out.println("😚😚😚😚😚😚😚😚😚😚😚😚😚");

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Page<CusManagement_orderDTO> getOrdersByCustomerId(String customerId, int page) {
        Pageable pageable = PageRequest.of(page,12);
        return orderRepository.getOrdersByCustomerId(customerId,pageable);

    }
}
