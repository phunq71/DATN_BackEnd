package com.main.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.main.dto.OrderPreviewDTO;
import com.main.dto.VoucherOrderDTO;
import com.main.service.CartService;
import com.main.service.VoucherService;
import com.main.utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CheckoutController {

    private final CartService cartService;
    private final VoucherService voucherService;

    @PostMapping("/opulentia_user/checkout")
    public String handleCheckout(@RequestParam("itemIDs") String itemIDsJson, Model model) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        List<Integer> itemIDs = mapper.readValue(itemIDsJson, new TypeReference<List<Integer>>() {});

        // Debug in console
        itemIDs.forEach(id -> System.out.println("🛒 Item ID: " + id));

        List<OrderPreviewDTO> list = cartService.getOrdersByItemIdsAndCustomerId(itemIDs, AuthUtil.getAccountID());

        int totalQuantity = list.stream()
                .mapToInt(OrderPreviewDTO::getQuantity)
                .sum();

        BigDecimal totalAmount = list.stream()
                .map(item -> {
                    BigDecimal price = item.getPrice();
                    BigDecimal discount = BigDecimal.valueOf(item.getDiscountPercent()); // discount là Byte
                    BigDecimal discountedPrice = price.subtract(
                            price.multiply(discount).divide(BigDecimal.valueOf(100))
                    );
                    return discountedPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Lấy danh sách Voucher
        List<VoucherOrderDTO> listVoucher = voucherService.getListVoucherFromOrder(totalAmount);
        System.out.println(listVoucher);
        model.addAttribute("listItems", list);
        String json = mapper.writeValueAsString(list);
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String jsonVoucher = mapper.writeValueAsString(listVoucher);

        model.addAttribute("totalQuantity", totalQuantity);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("listVoucher", listVoucher);
        model.addAttribute("listVoucherJson", jsonVoucher);
        model.addAttribute("listItemsJson", json);

        return "View/checkout";
    }

}
