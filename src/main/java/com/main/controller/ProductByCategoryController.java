package com.main.controller;

import com.main.dto.ProductByCategory;
import com.main.security.JwtService;
import com.main.service.CategoryService;
import com.main.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProductByCategoryController {
    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private JwtService jwtService;

    //hiển thị ds sp theo danh mục, có phân trang
    @GetMapping("/opulentia/{parent}/{child}/{page}")
    public String viewProductsByCategory(
            @PathVariable("parent") String parent,
            @PathVariable("child") String child,
            @PathVariable("page") int page,
            Model model
    ) {
        // Nếu child = "null", gán nó là null thật
        String childCategory = "null".equalsIgnoreCase(child) ? null : child;

        //gọi service
        Page<ProductByCategory> productPage = productService.getProductsByCategory(parent, childCategory, page);

        //lấy tên danh mục
        String parentCategoryName = categoryService.findNameById(parent);
        String childCategoryName = (childCategory != null) ? categoryService.findNameById(childCategory) : null;

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("parentCategory", parent);
        model.addAttribute("childCategory", child);
        model.addAttribute("parentCategoryName", parentCategoryName);
        model.addAttribute("childCategoryName", childCategoryName);


        return "View/ProductByCategory";
    }
}
