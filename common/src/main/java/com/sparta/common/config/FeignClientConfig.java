package com.sparta.common.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
public class FeignClientConfig implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        try {
            // SecurityContext에서 Authentication 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.isAuthenticated()) {
                Object credentials = authentication.getCredentials();
                if (credentials != null) {
                    // Authorization 헤더 추가
                    String token = "Bearer " + credentials.toString();
                    requestTemplate.header("Authorization", token);
                } else {
                    // 인증 정보는 있으나 자격 증명 누락
                    System.out.println("Credentials are null, unable to add Authorization header.");
                }
            } else {
                // 인증되지 않은 요청 처리
                System.out.println("Authentication is null or not authenticated.");
            }
        } catch (Exception e) {
            // 예외 처리 및 로깅
            System.out.println("Error occurred while applying FeignClientConfig: " + e.getMessage());
        }
    }
}
