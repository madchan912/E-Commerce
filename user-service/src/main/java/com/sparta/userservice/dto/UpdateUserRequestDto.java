package com.sparta.userservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequestDto {
    private String name;
    private String phoneNumber;
    private String address;
}
