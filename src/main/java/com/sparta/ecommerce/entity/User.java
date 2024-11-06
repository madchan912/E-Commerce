package com.sparta.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "app_user") // 테이블 이름을 "app_user"로 변경
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String password;
    private boolean isVerified; // 이메일 인증 여부
    private String phoneNumber; // 전화번호 추가
    private String address; // 주소추가
}
