package com.main.utils;

import com.main.security.CustomOAuth2User;
import com.main.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class AuthUtil {

    private AuthUtil() {
        // Ngăn tạo instance
    }

    /**
     * Lấy AccountID của user hiện tại.
     *
     * @return AccountID nếu đã đăng nhập, null nếu chưa đăng nhập.
     */
    public static String getAccountID() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof CustomUserDetails userDetails) {
            return userDetails.getAccountId();
        } else if (principal instanceof CustomOAuth2User oAuthUser) {
            return oAuthUser.getAccountId();
        }
        return null;
    }


    /**
     * Kiểm tra user đã đăng nhập hay chưa.
     *
     * @return true nếu đã đăng nhập, false nếu chưa đăng nhập.
     */
    public static boolean isLogin() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal instanceof CustomUserDetails || principal instanceof CustomOAuth2User;
    }


    /**
     * Lấy Role của user hiện tại.
     *
     * @return Role nếu đã đăng nhập, null nếu chưa đăng nhập.
     */
    public static String getRole() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof CustomUserDetails userDetails) {
            return userDetails.getRole();
        } else if (principal instanceof CustomOAuth2User oAuthUser) {
            return "ROLE_USER";
        }
        return null;
    }

    /**
     * Lấy tên của user hiện tại.
     *
     * @return tên nếu đã đăng nhập, null nếu chưa đăng nhập.
     */
    public static String getFullName() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof CustomUserDetails userDetails) {
            return userDetails.getFullName();
        } else if (principal instanceof CustomOAuth2User oAuthUser) {
            return oAuthUser.getFullName();
        }
        return null;
    }
}
