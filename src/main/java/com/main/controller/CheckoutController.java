package com.main.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public class CheckoutController {
    @PostMapping("/checkout")
    public String handleCheckout(@RequestParam("itemIDs") String itemIDsJson) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        List<String> itemIDs = mapper.readValue(itemIDsJson, new TypeReference<List<String>>() {});

        if (itemIDs == null || itemIDs.isEmpty()) {
            System.out.println("Không có sản phẩm nào được chọn.");
        } else {
            System.out.println("Danh sách itemIDs được chọn:");
            itemIDs.forEach(System.out::println);
        }

        return "View/checkout";
    }
}
