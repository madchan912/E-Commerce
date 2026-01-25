package com.sparta.userservice.service;

import com.sparta.common.util.JwtUtil;
import com.sparta.userservice.dto.SignupRequestDto;
import com.sparta.userservice.dto.UpdateUserRequestDto;
import com.sparta.userservice.dto.UserResponse;
import com.sparta.userservice.entity.User;
import com.sparta.userservice.entity.UserRoleEnum;
import com.sparta.userservice.entity.VerificationToken;
import com.sparta.userservice.repository.UserRepository;
import com.sparta.userservice.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;
    private final TokenService tokenService;
    private final StringRedisTemplate redisTemplate;

    // 회원가입 (유저 저장 + 인증 메일 발송)
    @Transactional
    public UserResponse singup(SignupRequestDto requestDto) {
        String email = requestDto.getEmail();
        String password = passwordEncoder.encode(requestDto.getPassword()); // BCrypt 암호화

        // 1. 이메일 중복 확인
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        // 2. 유저 엔티티 생성
        User user = User.builder()
                .email(email)
                .password(password)
                .name(requestDto.getName())
                .role(UserRoleEnum.USER)
                .phoneNumber(requestDto.getPhoneNumber())
                .address(requestDto.getAddress())
                .build();

        User savedUser = userRepository.save(user);

        // 3. 이메일 인증 토큰 생성 및 저장
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUserId(savedUser.getId());
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24)); // 24시간 유효
        tokenRepository.save(verificationToken);

        // 4.인증 이메일 발송
        String verificationLink = "http://localhost:8080/users/verify?token=" + token;
        emailService.sendEmail(
                email,
                "이메일 인증 요청",
                "안녕하세요, " + user.getName() + "님! \n다음 링크를 클릭하여 인증을 완료하세요: " + verificationLink
        );

        return new UserResponse(savedUser);
    }

    // 이메일 인증 처리
    @Transactional
    public void verifyEmail(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 토큰입니다."));
        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("토큰이 만료되었습니다.");
        }

        User user = userRepository.findById(verificationToken.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        user.confirmVerification();
    }

    // 로그인
    @Transactional
    public String login(String email, String password) {
        // 1. 유저 확인
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다."));

        // 2. 비밀번호 확인
        if (!passwordEncoder.matches(password, user.getPassword())) {
           throw new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        // 3. 인증 여부 확인
        if (!user.isVerified()) {
            throw new IllegalArgumentException("이메일 인증이 완료되지 않았습니다.");
        }

        // 4. 토큰 생성
        String token = jwtUtil.generateToken(email);

        // 5. Redis에 토큰 저장 (중복 로그인 관리 등)
        tokenService.saveUserToken(user.getId().toString(), token, jwtUtil.getExpiration(token));

        return token;
    }

    // 로그아웃
    public void logout(String token) {
        // 1. "Bearer " 제거
        String rawToken = token; // 원본 보존
        if (token != null && token.startsWith("Bearer ")) {
            rawToken = token.substring(7);
        }

        // 2. 만료 시간 가져오기
        long expiration = jwtUtil.getExpiration(rawToken);

        // 3. Redis 블랙리스트 등록 (핵심: 이걸로 막는 것)
        redisTemplate.opsForValue().set("blacklist:" + rawToken, "logout", expiration, TimeUnit.MILLISECONDS);

        // [추가] 4. 토큰에서 유저 ID 꺼내서, 로그인 리스트(Whitelist)에서도 삭제 (청소용)
        // (이 부분은 필수는 아니지만, Redis를 깔끔하게 유지하기 위함)
        String email = jwtUtil.getUserInfoFromToken(rawToken).getSubject();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            // Redis List에서 해당 토큰 값만 쏙 빼서 삭제
            // 키: "user:{id}:tokens"
            redisTemplate.opsForList().remove("user:" + user.getId() + ":tokens", 1, rawToken);
        }
    }

    // 모든 기기 로그아웃
    public void logoutAllDevices(Long userId) {
        tokenService.deleteUserTokens(userId.toString());
    }

    // 전체 조회 (관리자용)
    @Transactional(readOnly = true) //읽기 전용
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());
    }

    // 단건 조회
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = findUser(id);
        return new UserResponse(user);
    }

    // 정보 수정
    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequestDto requestDto) {
        User user = findUser(id);
        // 더티 체킹 (Dirty Checking): save 호출 없이 객체만 수정하면 DB 자동 업데이트
        user.update(requestDto.getName(), requestDto.getPhoneNumber(), requestDto.getAddress());
        return new UserResponse(user);
    }

    // 삭제
    @Transactional
    public void deleteUser(Long id) {
        User user = findUser(id);
        userRepository.delete(user);
    }

    // 공통 메서드: 유저 찾기 (없으면 예외 발생)
    private User findUser(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다.")
        );
    }
}
