package com.main.rest_controller;

import com.main.utils.AuthUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/opulentia")
public class UserRestController {
    @GetMapping("/isLogin")
    public ResponseEntity<Boolean> isLogin() {
        return ResponseEntity.ok(AuthUtil.isLogin());
    }
}

