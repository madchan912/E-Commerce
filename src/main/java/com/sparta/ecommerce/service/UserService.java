package com.sparta.ecommerce.service;

import com.sparta.ecommerce.entity.User;
import com.sparta.ecommerce.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 새로운 사용자를 추가합니다.
    public User createUser(User user) {
        return userRepository.save(user);
    }

    // 모든 사용자를 조회합니다.
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // ID로 특정 사용자를 조회합니다.
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // 특정 사용자의 정보를 업데이트합니다.
    public User updateUser(Long id, User userDetails) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setName(userDetails.getName());
                    user.setEmail(userDetails.getEmail());
                    user.setPassword(userDetails.getPassword());
                    return userRepository.save(user);
                }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    // 특정 사용자를 삭제합니다.
    public void deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }
}
