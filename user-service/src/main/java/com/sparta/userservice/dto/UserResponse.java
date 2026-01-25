package com.sparta.userservice.dto;

import com.sparta.userservice.entity.User;
import com.sparta.userservice.entity.UserRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // 기본 생성자 (JSON 역직렬화용)
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String name;
    private UserRoleEnum role;      // 권한 (프론트에서 관리자 버튼 노출 여부 판단)
    private String phoneNumber;     // 주문 시 자동입력용
    private String address;         // 주문 시 자동입력용
    private boolean isVerified;     // 인증 여부

    // 엔티티를 받아서 DTO로 변환하는 생성자 추가
    public UserResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.role = user.getRole();
        this.phoneNumber = user.getPhoneNumber();
        this.address = user.getAddress();
        this.isVerified = user.isVerified();
    }
}