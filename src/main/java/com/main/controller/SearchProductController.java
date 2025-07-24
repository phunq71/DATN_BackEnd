package com.main.controller;

import com.main.dto.ProductViewDTO;
import com.main.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SearchProductController {
    private final ProductService productService;

//    @GetMapping("/opulentia/search")
//    public String searchProducts(@RequestParam("keyword") String keyword, Model model) {
//        if (keyword == null || keyword.trim().isBlank()) {
//            model.addAttribute("products", List.of()); // Trả về danh sách rỗng
//            model.addAttribute("keyword", "");
//            return "View/searchProduct";
//        }
//
//        keyword = keyword.toLowerCase().trim();
//        List<ProductViewDTO> products = productService.searchProducts(keyword);
//
//        model.addAttribute("products", products);
//        model.addAttribute("keyword", keyword);
//
//        return "View/searchProduct";
//    }
}
