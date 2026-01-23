package com.sparta.userservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users") // [변경] 표준 이름인 'users'로 변경
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    private boolean isVerified; // 이메일 인증 여부
    private String phoneNumber; // 전화번호
    private String address;     // 주소

    public User(String email, String password, String name, UserRoleEnum role, String phoneNumber, String address) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.isVerified = false; // 기본값
    }

    // [추가] 정보 수정 메서드
    public void update(String name, String phoneNumber, String address) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }
}