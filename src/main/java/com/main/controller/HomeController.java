package com.main.controller;

import com.main.service.AccountService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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


	@GetMapping("/index")
	public String index(@RequestParam(value = "msg", required = false) String msg, Model model, HttpServletRequest request) {
		if ("logout".equals(msg)) {
			request.getSession(true);
			model.addAttribute("status", "success");
			model.addAttribute("messageLayout", "Đăng xuất thành công");
		} else if ("login".equals(msg)) {
			model.addAttribute("status", "success");
			model.addAttribute("messageLayout", "Đăng nhập thành công");
		}
		return "View/index";
	}





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
