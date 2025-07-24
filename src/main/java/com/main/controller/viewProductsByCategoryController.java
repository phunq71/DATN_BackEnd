package com.main.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.main.dto.ColorOption;
import com.main.dto.ProductViewDTO;
import com.main.entity.Product;
import com.main.security.JwtService;
import com.main.service.CategoryService;
import com.main.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class viewProductsByCategoryController {
    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private JwtService jwtService;

    @GetMapping("/opulentia/{parent}/{child}/{page}")
    public String viewProductsByCategory(
            @PathVariable("parent") String parent,
            @PathVariable("child") String child,
            @PathVariable("page") int page,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) BigDecimal priceMin,
            @RequestParam(required = false) BigDecimal priceMax,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) String targetCustomer,
            @RequestParam(required = false) String sort,

            Model model

    ) {
        if (keyword != null) {
            keyword = keyword.trim().replaceAll("\\s+", " ");
        }
        System.out.println("Sort: " + sort);
        // Chuẩn hóa giá trị parent/child nếu là "null"
        if ("null".equalsIgnoreCase(child) || child == null || child.isBlank()) child = null;
        if ("null".equalsIgnoreCase(parent) || parent == null || parent.isBlank()) parent = null;

        Pageable pageable = PageRequest.of(page, 12);

        // Gọi service lọc sản phẩm
        Page<ProductViewDTO> productsPage = productService.searchAndFilterAllProducts(
                keyword,
                color,
                brand,
                priceMin,
                priceMax,
                minRating,
                targetCustomer,
                child,
                parent,
                pageable,
                sort
        );

        // Lấy tên danh mục để hiển thị
        String parentCategoryName = parent != null ? categoryService.findNameById(parent) : null;
        String childCategoryName = child != null ? categoryService.findNameById(child) : null;

        // Load màu từ file JSON
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream inputStream = new ClassPathResource("static/data/Color.json").getInputStream();
            List<ColorOption> colors = Arrays.asList(mapper.readValue(inputStream, ColorOption[].class));
            model.addAttribute("colors", colors);
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("colors", List.of());
        }

        // Lấy danh sách thương hiệu duy nhất
        List<String> brandNames = productService.findAll().stream()
                .map(Product::getBrand)
                .filter(brandName -> brandName != null && !brandName.isBlank())
                .distinct()
                .collect(Collectors.toList());

        // Truyền dữ liệu sang view
        model.addAttribute("products", productsPage);
        model.addAttribute("brandProducts", brandNames);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productsPage.getTotalPages());
        model.addAttribute("parentCategory", parent);
        model.addAttribute("childCategory", child);
        model.addAttribute("parentCategoryName", parentCategoryName);
        model.addAttribute("childCategoryName", childCategoryName);

        // Giữ lại các lựa chọn đã chọn
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedColor", color);
        model.addAttribute("selectedBrand", brand);
        model.addAttribute("selectedTarget", targetCustomer);
        model.addAttribute("selectedSort", sort);
        model.addAttribute("priceMin", priceMin);
        model.addAttribute("priceMax", priceMax);
        model.addAttribute("minRating", minRating);

        StringBuilder filterQuery = new StringBuilder();
        if (keyword != null && !keyword.isBlank()) filterQuery.append("&keyword=").append(URLEncoder.encode(keyword, StandardCharsets.UTF_8));
        if (color != null && !color.isBlank()) filterQuery.append("&color=").append(URLEncoder.encode(color, StandardCharsets.UTF_8));
        if (brand != null && !brand.isBlank()) filterQuery.append("&brand=").append(URLEncoder.encode(brand, StandardCharsets.UTF_8));
        if (priceMin != null) filterQuery.append("&priceMin=").append(priceMin);
        if (priceMax != null) filterQuery.append("&priceMax=").append(priceMax);
        if (minRating != null) filterQuery.append("&minRating=").append(minRating);
        if (targetCustomer != null && !targetCustomer.isBlank()) filterQuery.append("&targetCustomer=").append(URLEncoder.encode(targetCustomer, StandardCharsets.UTF_8));
        if (sort != null && !sort.isBlank()) filterQuery.append("&sort=").append(sort);

// Bắt đầu bằng dấu "?" nếu có tham số
        String finalFilterQuery = filterQuery.length() > 0 ? "?" + filterQuery.substring(1) : "";
        model.addAttribute("filterQuery", finalFilterQuery);



        return "View/viewProductsByCategory";
    }

}
