package com.main.security;

import com.main.entity.Account;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(UserDetails userDetails) {
        CustomUserDetails user = (CustomUserDetails) userDetails;
        return generateToken(user, accessTokenExpiration);
    }


    public String generateRefreshToken(UserDetails userDetails) {
        CustomUserDetails user = (CustomUserDetails) userDetails;
        return generateToken(user, refreshTokenExpiration);
    }


    public String generateToken(CustomUserDetails user, long expirationMillis) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .setSubject(user.getUsername()) // là email
                .claim("accountId", user.getAccountId())
                .claim("provider", user.getProvider())       // 👈 đã dùng được
                .claim("providerId", user.getProviderId())   // 👈 đã dùng được
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }




    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // Bạn có thể log lỗi cụ thể ở đây nếu cần
            return false;
        }
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(getSignKey())
                .parseClaimsJws(token)
                .getBody();
    }



    public String getProviderFromToken(String token) {
        return getAllClaimsFromToken(token).get("provider", String.class);
    }

    public String getProviderIdFromToken(String token) {
        return getAllClaimsFromToken(token).get("providerId", String.class);
    }

    public String getAccountIdFromToken(String token) {
        return getAllClaimsFromToken(token).get("accountId", String.class);
    }

    public String getEmailFromToken(String token) {
        return getAllClaimsFromToken(token).getSubject(); // subject là email
    }




}






