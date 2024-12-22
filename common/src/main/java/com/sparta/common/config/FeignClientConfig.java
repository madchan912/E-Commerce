package com.sparta.common.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
public class FeignClientConfig implements RequestInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(FeignClientConfig.class);

    @Override
    public void apply(RequestTemplate requestTemplate) {
        logger.info("Feign Client Request Interceptor started.");

        // SecurityContext에서 Authentication 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            logger.warn("SecurityContextHolder returned null Authentication.");
            return;
        }

        if (!authentication.isAuthenticated()) {
            logger.warn("Authentication is not authenticated. Principal: {}", authentication.getPrincipal());
            return;
        }

        logger.info("Authentication is authenticated. Principal: {}", authentication.getPrincipal());

        Object credentials = authentication.getCredentials();
        if (credentials == null) {
            logger.warn("Authentication credentials are null.");
            return;
        }

        String token = "Bearer " + credentials.toString();
        logger.info("Adding Authorization header to Feign request: {}", token);

        // Authorization 헤더 추가
        requestTemplate.header("Authorization", token);
    }
}
