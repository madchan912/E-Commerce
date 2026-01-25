package com.sparta.userservice.entity;

import com.sparta.common.entity.Timestamped;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = PROTECTED)
public class User extends Timestamped {

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

    private boolean isVerified;
    private String phoneNumber;
    private String address;

    @Builder
    public User(String email, String password, String name, UserRoleEnum role, String phoneNumber, String address) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.isVerified = false; // 생성 시점엔 무조건 false
    }

    // 정보 수정 비지니스 로직
    public void update(String name, String phoneNumber, String address) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    // 권한 변경
    public void confirmVerification() {
        this.isVerified = true;
    }
}