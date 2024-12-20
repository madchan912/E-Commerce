package com.sparta.userservice.service;

import com.sparta.common.dto.SignUpRequestDto;
import com.sparta.common.util.AESUtil;
import com.sparta.common.util.JwtUtil;
import com.sparta.userservice.entity.User;
import com.sparta.userservice.entity.VerificationToken;
import com.sparta.userservice.repository.UserRepository;
import com.sparta.userservice.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;
    private final TokenService tokenService;

    // 회원가입 및 이메일 인증 토큰 생성
    public void signUp(SignUpRequestDto requestDto) {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new RuntimeException("Email is already registered");
        }

        User user = new User();
        user.setName(AESUtil.encrypt(requestDto.getName())); // 이름 암호화
        user.setEmail(AESUtil.encrypt(requestDto.getEmail())); // 이메일 암호화
        user.setPassword(passwordEncoder.encode(requestDto.getPassword())); // 비밀번호 해싱
        user.setPhoneNumber(AESUtil.encrypt(requestDto.getPhoneNumber())); // 전화번호 암호화
        user.setAddress(AESUtil.encrypt(requestDto.getAddress())); // 주소 암호화
        user.setVerified(false); // 이메일 인증 전

        User savedUser = userRepository.save(user);

        // 이메일 인증 토큰 생성
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUserId(savedUser.getId());
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24)); // 24시간 유효

        tokenRepository.save(verificationToken);

        // 이메일 인증 링크 생성 및 전송
        String verificationLink = "http://localhost:8080/auth/verify?token=" + token;
        emailService.sendEmail(
                requestDto.getEmail(),
                "이메일 인증 요청",
                "안녕하세요, " + requestDto.getName() + "님!\n다음 링크를 클릭하여 이메일을 인증하세요: " + verificationLink
        );
    }

    // 이메일 인증 처리
    public void verifyEmail(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token has expired");
        }

        User user = userRepository.findById(verificationToken.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setVerified(true); // 사용자 활성화
        userRepository.save(user);
    }

    // 로그인 기능 추가
    public String login(String email, String password) {
        // 이메일을 암호화한 후 데이터베이스에서 조회
        Optional<User> userOptional = userRepository.findByEmail(AESUtil.encrypt(email));
        
        // 사용자 존재 여부
        if (userOptional.isEmpty()) {
            throw new RuntimeException("Invalid email or password");
        }
        
        User user = userOptional.get();
        
        // 암호 일치 여부
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        // 이메일 인증 여부
        if (!user.isVerified()) {
            throw new RuntimeException("Email not verified");
        }

        // 이메일 복호화 하여 JWT 토큰생성
        String decryptedEmail = AESUtil.decrypt(user.getEmail());
        String token = jwtUtil.generateToken(decryptedEmail);

        // 사용자 ID를 Redis에 저장
        String userId = user.getId().toString();
        long expiration = jwtUtil.getExpiration(token);
        tokenService.saveUserToken(userId, token, expiration);

        // Bearer 토큰 반환
        return "Bearer " + token;
    }

    // 로그아웃 시 토큰을 블랙리스트에 추가
    public void logout(String token) {
        // 토큰의 만료 시간을 가져옵니다.
        long expiration = jwtUtil.getExpiration(token);
        String key = "blacklist:" + token;

        // Redis에 토큰을 저장하고, 만료 시간까지 유지
        redisTemplate.opsForValue().set(key, "logout", expiration, TimeUnit.MILLISECONDS);
    }

    // 모든 기기에서 로그아웃 처리
    public void logoutAllDevices(String userId) {
        // Redis에서 사용자별 저장된 모든 토큰을 조회
        tokenService.deleteUserTokens(userId);
    }
}
