package com.sparta.ecommerce.controller;

import com.sparta.ecommerce.dto.SignUpRequestDto;
import com.sparta.ecommerce.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 회원가입
     *
     * @param requestDto 회원가입 요청 정보
     * @return 회원가입 성공 메시지
     */
    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody SignUpRequestDto requestDto) {
        authService.signUp(requestDto);
        return ResponseEntity.ok("회원가입 성공! 이메일을 확인해주세요.");
    }

    /**
     * 이메일 인증
     *
     * @param token 이메일 인증 토큰
     * @return 이메일 인증 성공 메시지
     */
    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok("이메일 인증이 완료되었습니다!");
    }

    /**
     * 로그인
     *
     * @param email 로그인 이메일
     * @param password 로그인 암호
     * @return JWT 토큰  생성
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password) {
        String token = authService.login(email, password);
        return ResponseEntity.ok(token); // JWT 토큰 반환
    }
}
