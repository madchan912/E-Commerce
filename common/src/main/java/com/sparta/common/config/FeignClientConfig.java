package com.sparta.common.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
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
                    log.info("Credentials are null, unable to add Authorization header.");
                }
            } else {
                // 인증되지 않은 요청 처리
                log.info("Authentication is null or not authenticated.");
            }
        } catch (Exception e) {
            // 예외 처리 및 로깅
            log.error("Error occurred while applying FeignClientConfig: " + e.getMessage());
        }
    }
}
