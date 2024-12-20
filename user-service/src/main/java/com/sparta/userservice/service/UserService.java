package com.sparta.userservice.service;

import com.sparta.common.util.AESUtil;
import com.sparta.userservice.entity.User;
import com.sparta.userservice.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    // 모든 사용자를 조회
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // ID로 특정 사용자를 조회
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id).map(user -> {
            user.setName(AESUtil.decrypt(user.getName())); // 이름 복호화
            user.setEmail(AESUtil.decrypt(user.getEmail())); // 이메일 복호화
            // 비밀번호는 복호화하지 않음 (단방향 해시)
            return user;
        });
    }

    // 특정 사용자의 정보를 업데이트
    public User updateUser(Long id, User userDetails) {
        return userRepository.findById(id).map(user -> {
            user.setName(AESUtil.encrypt(userDetails.getName())); // 이름 암호화
            user.setEmail(AESUtil.encrypt(userDetails.getEmail())); // 이메일 암호화
            user.setPassword(passwordEncoder.encode(userDetails.getPassword())); // 비밀번호 해싱
            return userRepository.save(user);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    // 특정 사용자를 삭제
    public void deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }
}
