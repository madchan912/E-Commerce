package com.sparta.ecommerce.controller;

import com.sparta.ecommerce.entity.User;
import com.sparta.ecommerce.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 모든 사용자 조회
     *
     * @return 사용자 목록
     */
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * ID로 특정 사용자 조회
     *
     * @param id 조회할 사용자의 ID
     * @return 조회된 사용자 정보
     */
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id).orElse(null);
    }

    /**
     * 특정 사용자 정보 업데이트
     *
     * @param id 업데이트할 사용자의 ID
     * @param userDetails 업데이트할 사용자 정보
     * @return 업데이트된 사용자 정보
     */
    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        return userService.updateUser(id, userDetails);
    }

    /**
     * 특정 사용자 삭제
     *
     * @param id 삭제할 사용자의 ID
     */
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
