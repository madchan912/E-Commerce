package com.sparta.ecommerce.dto;

import lombok.Data;

@Data
public class SignUpRequestDto {
    private String name;
    private String email;
    private String password;
    private String phoneNumber;
    private String address;
}
