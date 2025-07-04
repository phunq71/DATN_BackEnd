package com.main.config;

import com.main.service.CategoryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.time.Year;

@Component
public class GlobalModelInterceptor implements HandlerInterceptor {
    private final CategoryService categoryService;

    public GlobalModelInterceptor(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           ModelAndView modelAndView) throws Exception {
        if (modelAndView != null) {
            modelAndView.addObject("categoryMenu", categoryService.getCategoryMenu());
        }
    }
}
