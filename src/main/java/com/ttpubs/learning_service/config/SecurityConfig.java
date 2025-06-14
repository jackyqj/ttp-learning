package com.ttpubs.learning_service.config;

import com.ttpubs.learning_service.security.JwtTokenFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtTokenFilter jwtTokenFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // 禁用 CSRF，因为我们将在前端处理
                .headers(frameOptions -> frameOptions.disable()) // 允许 H2 控制台的 iframe
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/hello", "/api/auth/register", "/api/auth/login", "/h2-console/**").anonymous() // 允许匿名访问注册和登录接口
                        .anyRequest().authenticated() // 其他所有请求都需要认证
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 使用无状态会话
                )
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class); // 添加 JWT 过滤器
        return http.build();
    }
}