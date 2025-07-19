package com.main.security;



import com.main.entity.Account;
import com.main.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Autowired private JwtService jwtService;
    @Autowired private AccountRepository accountRepo;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
        Account account = accountRepo.findById(oauthUser.getAccountId()).orElseThrow();

        boolean rememberMe = getRememberMeFromCookie(request);
        System.out.println("🔐 OAuth2 login - rememberMe: " + rememberMe);

        String accessToken = jwtService.generateAccessToken(new CustomUserDetails(account));
        String refreshToken = jwtService.generateRefreshToken(new CustomUserDetails(account));

        long accessTokenMaxAge = 15 * 60;
        long refreshTokenMaxAge = rememberMe ? 7 * 24 * 60 * 60 : -1;     // 7 ngày hoặc không set

        // Cookie: Access Token
        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .path("/")
                .maxAge(accessTokenMaxAge)
                .sameSite("Lax")
                .build();

        response.addHeader("Set-Cookie", accessTokenCookie.toString());

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .path("/")
                .maxAge(refreshTokenMaxAge)
                .sameSite("Lax")
                .build();

        response.addHeader("Set-Cookie", refreshTokenCookie.toString());

        // ✅ Xóa cookie rememberMe tạm sau khi dùng
        ResponseCookie deleteRememberMe = ResponseCookie.from("rememberMe", "")
                .path("/").maxAge(0).build();
        String redirectUrl = "/index"; // fallback mặc định

        // Lấy cookie từ request
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("redirectAfterLogin".equals(cookie.getName())) {
                    redirectUrl = URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8);
                    break;
                }
            }
        }

        // Clear cookie để tránh dùng lại
        ResponseCookie clearCookie = ResponseCookie.from("redirectAfterLogin", "")
                .maxAge(0)
                .path("/")
                .build();
        response.addHeader("Set-Cookie", clearCookie.toString());
        Cookie flagCookie = new Cookie("flag", "true");
        flagCookie.setPath("/"); // Áp dụng toàn site
        flagCookie.setMaxAge(60); // Tồn tại 60s, hoặc bạn có thể để -1 (session)
        response.addCookie(flagCookie);
        System.out.println(redirectUrl);
        if(redirectUrl.equals("/opulentia_user/checkout")) {
            Cookie isMergeCart = new Cookie("isMergeCart", "true");
            isMergeCart.setPath("/");
            isMergeCart.setMaxAge(60);
            response.addCookie(isMergeCart);
        }
        response.sendRedirect(redirectUrl);

    }

    private boolean getRememberMeFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return false;
        for (Cookie cookie : request.getCookies()) {
            if ("rememberMe".equals(cookie.getName())) {
                return Boolean.parseBoolean(cookie.getValue());
            }
        }
        return false;
    }
}


