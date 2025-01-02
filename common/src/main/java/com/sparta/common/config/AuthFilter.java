package com.sparta.common.config;

import com.sparta.common.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

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
        if (requestURI.startsWith("/auth") || requestURI.startsWith("/products") || requestURI.startsWith("/performances")) {
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

        if(token != null && jwtUtil.validateToken(token)){
            System.out.println("Token is valid.");

            // 토큰에서 사용자 정보 추출
            String email = jwtUtil.extractEmail(token);
            System.out.println("Authenticated user: " + email);

            // SecurityContextHolder에 Authentication 설정
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    email, null, List.of(new SimpleGrantedAuthority("ROLE_USER"))); // ROLE_USER 권한 설정
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        // 다른 유효성 검사 로직 추가
        filterChain.doFilter(request, response);
    }
}
