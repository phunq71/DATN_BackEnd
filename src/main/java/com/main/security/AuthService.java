package com.main.security;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public void authenticate(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()
                )
        );
    }

    public Map<String, ResponseCookie> generateTokenCookies(UserDetails userDetails, boolean rememberMe) {
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        long accessTokenMaxAge = 15 * 60;
        long refreshTokenMaxAge = rememberMe ? 7 * 24 * 60 * 60 : -1; // 7 ngày nếu nhớ, -1 nếu không

        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(accessTokenMaxAge)
                .sameSite("Lax")
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(refreshTokenMaxAge)
                .sameSite("Lax")
                .build();

        Map<String, ResponseCookie> cookies = new HashMap<>();
        cookies.put("accessToken", accessTokenCookie);
        cookies.put("refreshToken", refreshTokenCookie);
        return cookies;
    }


    public UserDetails loadUser(String email) {
        return userDetailsService.loadUserByUsername(email);
    }

    public boolean isValidToken(String token) {
        return jwtService.validateToken(token);
    }

    public String getEmailFromToken(String token) {
        return jwtService.getEmailFromToken(token);
    }

    public JwtResponse refreshToken(String refreshToken) {
        String email = getEmailFromToken(refreshToken);
        UserDetails userDetails = loadUser(email);
        String newAccessToken = jwtService.generateAccessToken(userDetails);
        String newRefreshToken = jwtService.generateRefreshToken(userDetails);
        return new JwtResponse(newAccessToken, newRefreshToken);
    }
}

