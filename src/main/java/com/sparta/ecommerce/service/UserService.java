package com.sparta.ecommerce.service;

import com.sparta.ecommerce.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 사용자 관련 비즈니스 로직 추가 가능
}
