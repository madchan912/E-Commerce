package com.sparta.userservice.controller;

import com.sparta.userservice.dto.LoginRequestDto;
import com.sparta.userservice.dto.SignupRequestDto;
import com.sparta.userservice.dto.UpdateUserRequestDto;
import com.sparta.userservice.dto.UserResponse;
import com.sparta.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 회원가입
     * POST /users/signup
     */
    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@RequestBody SignupRequestDto requestDto) {
        UserResponse response = userService.singup(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 로그인
     * POST /users/login
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto requestDto) {
        String token = userService.login(requestDto.getEmail(), requestDto.getPassword());
        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + token)
                .body("Bearer " + token);
    }

    /**
     * 로그아웃
     * POST /users/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization")  String token) {
        userService.logout(token);
        return ResponseEntity.ok("로그아웃 성공");
    }

    /**
     * 이메일 인증
     * GET /users/verify?token=...
     */
    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        userService.verifyEmail(token);
        return ResponseEntity.ok("이메일 인증이 완료되었습니다.");
    }

    /**
     * 모든 사용자 조회 (관리자용)
     * GET /users
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * ID로 사용자 조회
     * GET /users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * 사용자 정보 수정
     * PUT /users/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequestDto requestDto) {
        return ResponseEntity.ok(userService.updateUser(id, requestDto));
    }

    /**
     * 사용자 삭제
     * DELETE /users/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("유저 삭제 완료");
    }
}
