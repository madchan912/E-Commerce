package com.sparta.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", ex.getStatusCode().value()); // getStatusCode() 사용
        body.put("error", HttpStatus.valueOf(ex.getStatusCode().value()).getReasonPhrase()); // 상태 코드에 따른 메시지
        body.put("message", ex.getReason()); // 추가된 메시지
        body.put("path", ServletUriComponentsBuilder.fromCurrentRequest().toUriString());

        return new ResponseEntity<>(body, ex.getStatusCode());
    }
}
