package com.sparta.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime;

    @Autowired
    @Qualifier("jwtRedisTemplate")
    private RedisTemplate<String, String> redisTemplate;

    // 토큰 생성
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    // 토큰에서 이메일 추출
    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    // 토큰이 만료되었는지 확인
    public boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    // 토큰에서 클레임(Claims) 추출
    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }

    // 만료 시간 가져오기
    public long getExpiration(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
        return claims.getExpiration().getTime();
    }

    // 블랙리스트에 토큰이 있는지 확인
    public boolean isTokenBlacklisted(String token) {
        String key = "blacklist:" + token;
        return redisTemplate.hasKey(key); // Redis에서 키가 존재하는지 확인
    }

    // Authorization 헤더에서 JWT 토큰을 추출하는 메서드
    public String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // JWT 토큰의 유효성을 검증하는 메서드
    public boolean validateToken(String token) {
        try {
            // 서명 키를 사용해 토큰의 유효성을 확인
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true; // 유효한 토큰일 경우
        } catch (Exception e) {
            System.out.println("Invalid token: " + e.getMessage());
            return false; // 유효하지 않은 토큰일 경우
        }
    }

}
