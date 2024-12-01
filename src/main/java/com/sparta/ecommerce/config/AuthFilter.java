package com.sparta.ecommerce.config;

import com.sparta.ecommerce.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    // 모든 HTTP 요청에 대해 실행되는 필터 메서드
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // URI 로깅
        String requestURI = request.getRequestURI();
        System.out.println("Request URI: " + requestURI);

        // 특정 경로는 필터 로직을 건너뜁니다.
        if (requestURI.startsWith("/auth") || requestURI.startsWith("/products")) {
            filterChain.doFilter(request, response);
            return;
        }

        // JwtUtil을 사용해 토큰을 추출
        String token = jwtUtil.extractTokenFromRequest(request);
        System.out.println("Received Token: " + token);

        // 토큰 검증 및 추가 로직 처리
        if (token != null && jwtUtil.isTokenBlacklisted(token)) {
            System.out.println("Token is blacklisted.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        System.out.println("Token is valid.");
        // 다른 유효성 검사 로직 추가
        filterChain.doFilter(request, response);
    }
}
