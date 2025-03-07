package com.sparta.userservice.controller;

import com.sparta.common.dto.LoginRequest;
import com.sparta.common.dto.SignUpRequestDto;
import com.sparta.common.util.JwtUtil;
import com.sparta.userservice.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

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
     * @param loginRequest 로그인 요청 데이터 (이메일과 암호 포함)
     * @return JWT 토큰 생성
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        // 이메일과 비밀번호를 LoginRequest 객체로 받음
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        // 로그인 처리
        String token = authService.login(email, password);
        return ResponseEntity.ok(token); // JWT 토큰 반환
    }

    /**
     * 로그아웃
     *
     * @param request HTTP 요청 객체
     * @return 로그아웃 성공 메시지 또는 오류 메시지
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        // JwtUtil을 사용해 토큰을 추출
        String token = jwtUtil.extractTokenFromRequest(request);

        // 토큰이 없거나 유효하지 않은 경우 401 반환
        if (token == null || !jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다."); // 401 Unauthorized
        }

        // 토큰이 이미 블랙리스트에 등록되어 있는 경우
        if (jwtUtil.isTokenBlacklisted(token)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 블랙리스트에 등록된 토큰입니다.");
        }

        // 블랙리스트에 추가하여 로그아웃 처리
        authService.logout(token);

        // 성공 메시지 반환
        return ResponseEntity.ok("로그아웃 성공!");
    }

    /**
     * 모든 기기에서 로그아웃
     *
     * @param userId 사용자 ID
     * @return 로그아웃 성공 메시지
     */
    @PostMapping("/logout/all")
    public ResponseEntity<String> logoutAllDevices(@RequestParam String userId) {
        authService.logoutAllDevices(userId); // 모든 기기에서 로그아웃 처리
        return ResponseEntity.ok("모든 기기에서 로그아웃되었습니다.");
    }
}
