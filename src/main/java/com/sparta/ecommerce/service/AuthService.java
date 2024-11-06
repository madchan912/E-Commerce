package com.sparta.ecommerce.service;

import com.sparta.ecommerce.dto.SignUpRequestDto;
import com.sparta.ecommerce.entity.User;
import com.sparta.ecommerce.entity.VerificationToken;
import com.sparta.ecommerce.repository.UserRepository;
import com.sparta.ecommerce.repository.VerificationTokenRepository;
import com.sparta.ecommerce.util.AESUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

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
}
