package com.main.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.main.dto.ColorOption;
import com.main.dto.ProductByCategoryDTO;
import com.main.dto.ProductViewDTO;
import com.main.entity.Product;
import com.main.repository.ProductRepository;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ProductByCategoryController {
//    @Autowired
//    private ProductService productService;
//    @Autowired
//    private CategoryService categoryService;
//    @Autowired
//    private JwtService jwtService;
//
//    //hiển thị ds sp theo danh mục, có phân trang và lọc
//    @GetMapping("/opulentia/{parent}/{child}/{page}")
//    public String viewProductsByCategory(
//            @PathVariable("parent") String parent,
//            @PathVariable("child") String child,
//            @PathVariable("page") int page,
//            @RequestParam(required = false) String color,
//            @RequestParam(required = false) String brand,
//            @RequestParam(required = false) BigDecimal priceMin,
//            @RequestParam(required = false) BigDecimal priceMax,
//            @RequestParam(required = false) Double minRating,
//            @RequestParam(required = false) String targetCustomer,
//            @RequestParam(required = false) String sort,
//            Model model )
//    {
//        if (child != null && (child.isBlank() || child.equalsIgnoreCase("null"))) {
//            child = null;
//        }
//        if (parent != null && (parent.isBlank() || parent.equalsIgnoreCase("null"))) {
//            parent = null;
//        }
//
//        System.err.println("CL: " + color + " " + brand + " " + priceMin + " " + priceMax + " " + minRating + " " + targetCustomer + " " + sort + " " + child + " " + parent);
//                Pageable pageable = PageRequest.of(page, 12);
//                Page<ProductViewDTO> productss = productService.filterProductsWithReviewOnly(
//                        color,
//                        brand,
//                        priceMin,
//                        priceMax,
//                        minRating,
//                        targetCustomer,
//                        child,
//                        parent,
//                        pageable
//                );
//                System.err.println("Có khnggggg" + productss.getContent().size() + "   " + parent);
//                model.addAttribute("products", productss);
//        // Nếu child = "null", gán nó là null thật
//        String childCategory = "null".equalsIgnoreCase(child) ? null : child;
//
//        //gọi service
//        Page<ProductViewDTO> productPage = productService.getProductsByCategory(parent, childCategory, page);
//
//        //lấy tên danh mục
//        String parentCategoryName = categoryService.findNameById(parent);
//        String childCategoryName = (childCategory != null) ? categoryService.findNameById(childCategory) : null;
//        System.out.println(page);
//        //Hiện màu từ file color.json
//        try {
//            ObjectMapper mapper = new ObjectMapper();
//            InputStream inputStream = new ClassPathResource("static/data/Color.json").getInputStream();
//            List<ColorOption> colors = Arrays.asList(mapper.readValue(inputStream, ColorOption[].class));
//
//            model.addAttribute("colors", colors);
//        } catch (IOException e) {
//            e.printStackTrace(); // hoặc log lỗi
//            model.addAttribute("colors", List.of()); // fallback nếu lỗi
//        }
//        List<Product> products = productService.findAll();
//        List<String> brandNames = products.stream()
//                .map(Product::getBrand)
//                .filter(brands -> brands != null && !brands.isBlank())
//                .distinct()
//                .collect(Collectors.toList());
//
//        model.addAttribute("brandProducts", brandNames);
//        model.addAttribute("currentPage", page);
//        model.addAttribute("totalPages", productPage.getTotalPages());
//        model.addAttribute("parentCategory", parent);
//        model.addAttribute("childCategory", child);
//        model.addAttribute("parentCategoryName", parentCategoryName);
//        model.addAttribute("childCategoryName", childCategoryName);
//
//        return "View/ProductByCategory2";
//    }
}
