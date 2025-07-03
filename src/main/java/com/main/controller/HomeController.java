package com.main.controller;

import com.main.entity.Product;
import com.main.repository.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {
//	@GetMapping("/home")
//	public String home() {
//		return "View/Home";
//	}

//	@GetMapping("/header")
//	public String header() {
//		return "Layout/header";
//	}
//
//	@GetMapping("/footer")
//	public String footer() {
//		return "Layout/footer";
//	}
    @Autowired
ProductRepository productRepository;

    @GetMapping({"/viewAll"})
    public String viewAll() {
        List<Product> products = productRepository.findByParentCategoryOnly2("A0001");
        System.out.println(products.size());
        return "View/highlightProducts";
    }

    @GetMapping("/index")
    public String index(@RequestParam(value="login", required = false) String login,  Model model) {
        if (login != null) {
            model.addAttribute("status", "success");
            model.addAttribute("messageLayout", "Đăng nhập thành công");
        }
        return "View/index";
    }

    @GetMapping("/index2")
    public String index( Model model, HttpServletRequest request) {
        model.addAttribute("status", "success");
        model.addAttribute("messageLayout", "Đăng xuất thành công");
        return "View/index";
    }
//
//	@GetMapping("/viewAll")
//	public String viewAll() {
//		return "View/highlightProducts";
//	}
//
//	@GetMapping("/detail")
//	public String detail() {
//		return "View/productDetail";
//	}
//
//	@GetMapping("/cart")
//	public String cart() {
//		return "View/Cart";
//	}
//
//	@GetMapping("/checkout")
//	public String checkout() {
//		return "View/checkout";
//	}
//
//	@GetMapping("/qrPay")
//	public String qrPay() {
//		return "View/qrPay";
//	}
//
//	@GetMapping("/allOrders")
//	public String allOrders() {
//		return "View/allOrders";
//	}
//
	@GetMapping("/edit-profile")
	public String editProfile() {
		return "View/edit-profile";
	}
//	@GetMapping("/orderDetail")
//	public String orderDetail() {
//		return "View/orderDetail";
//	}
//
//	@GetMapping("/return")
//	public String RProduct() {
//		return "View/ReturnProduct";
//	}
//
//	@GetMapping("/review")
//	public String review() {
//		return "View/review";
//	}
//	//
//	@GetMapping("/bottom-navigation")
//	public String bottomNavigation() {
//		return "Layout/bottom-navigation";
//	}
}
