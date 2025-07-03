package com.main.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

    private final OAuth2User oAuth2User;
    private final String accountId;

    public CustomOAuth2User(OAuth2User oAuth2User, String accountId) {
        this.oAuth2User = oAuth2User;
        this.accountId = accountId;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oAuth2User.getAuthorities();
    }

    @Override
    public String getName() {
        return accountId; // 👈 đây chính là username trả về khi gọi authentication.getName()
    }

    public String getAccountId() {
        return accountId;
    }

    public String getEmail() {
        return (String) oAuth2User.getAttributes().get("email");
    }

    public String getFullName() {
        return (String) oAuth2User.getAttributes().get("name");
    }

    public String getPicture() {
        return (String) oAuth2User.getAttributes().get("picture");
    }
}
