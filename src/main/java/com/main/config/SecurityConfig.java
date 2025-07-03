package com.main.config;


import com.main.security.CustomAuthenticationSuccessHandler;
import com.main.security.CustomLoginFailureHandler;
import com.main.security.CustomOAuth2UserService;
import com.main.security.OAuth2RefererSavingFilter;
import com.main.serviceImpl.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private CustomAuthenticationSuccessHandler successHandler;

    @Autowired
    private CustomLoginFailureHandler customLoginFailureHandler;

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    private OAuth2RefererSavingFilter oAuth2RefererSavingFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/opulentia/rest/cart/**") // ✅ cấu hình CSRF riêng
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/opulentia/user/**").authenticated()
                        .requestMatchers("/auth", "/auth/login","/auth/**", "/index", "/logo/**", "/js/**",
                                "/data/**", "/test/**","/.well-known/**", "/opulentia/**", "/uploads/**" )
                        .permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/auth")
                        .loginProcessingUrl("/auth/login")
                        .failureHandler(customLoginFailureHandler)
                        .successHandler(successHandler)
                        .permitAll()
                )
                .addFilterBefore(new OAuth2RefererSavingFilter(), OAuth2LoginAuthenticationFilter.class)
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/auth")
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .successHandler(successHandler)
                )

                .rememberMe(remember -> remember
                        .key("v3qJHiO/ffKD+oLXkCMnyImZ/TOm8Rds6IZ6Xd6Cp456J8Ogh+qUYoUchFN2FliZwdTJa1lbJ5tUUpnJQOx/Ew==")
                        .tokenValiditySeconds(7 * 24 * 60 * 60)
                        .userDetailsService(userDetailsService)
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/index2")
                        .deleteCookies("JSESSIONID", "remember-me") // 👈 thêm dòng này
                        .permitAll()
                );

        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
        return authBuilder.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}



