package com.sparta.common.config;

import com.sparta.common.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String tokenValue = jwtUtil.getJwtFromHeader(request);

        if (StringUtils.hasText(tokenValue)) {

            // 1. Redis 블랙리스트 확인
            if (redisTemplate.hasKey("blacklist:" + tokenValue)) {
                log.warn("로그아웃된 토큰입니다.");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // 2. 토큰 유효성 검사
            if (!jwtUtil.validateToken(tokenValue)) {
                log.error("유효하지 않은 토큰입니다.");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // 3. 토큰에서 정보 꺼내기 (DB 조회 X)
            Claims info = jwtUtil.getUserInfoFromToken(tokenValue);

            try {
                // DB 조회 없이 토큰 정보만으로 인증 객체 생성
                setAuthentication(info.getSubject(), info.get("auth"));
            } catch (Exception e) {
                log.error(e.getMessage());
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    // 인증 객체 생성 및 저장
    public void setAuthentication(String email, Object roleObj) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(email, roleObj);
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    // 핵심: User 엔티티 없이 인증 객체 만들기
    private Authentication createAuthentication(String email, Object roleObj) {
        String role = roleObj != null ? roleObj.toString() : "USER"; // 역할이 없으면 기본값

        // 권한 목록 생성
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(role));

        // Principal에 User 객체 대신 'email(String)'이나 'UserDetailsImpl'을 넣음
        // 여기서는 간단하게 email(String)을 넣어서 가볍게 처리
        return new UsernamePasswordAuthenticationToken(email, null, authorities);
    }
}
