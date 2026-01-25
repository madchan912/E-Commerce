package com.sparta.userservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequestDto {
    private String email;
    private String password;
    private String name;
    private String phoneNumber;
    private String address;
    private boolean admin =  false;
    private String adminToken = "";
}
