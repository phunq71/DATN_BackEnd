package com.main.rest_controller;


import com.main.utils.AuthUtil;
import org.aspectj.apache.bcel.Repository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class CheckAuthAdminRestController {
    @GetMapping("/checkAdmin")
    public ResponseEntity<?> checkAdmin() {
        if (AuthUtil.getRole().equals("ROLE_ADMIN")) {
            return ResponseEntity.ok(true);
        }else {
            return ResponseEntity.ok(false);
        }
    }
}
