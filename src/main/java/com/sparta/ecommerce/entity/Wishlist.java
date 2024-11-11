package com.sparta.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "wishlist")
@Data
public class Wishlist {

    @Id
    private Long userId;  // 유저 ID를 기본키로 설정

    @ElementCollection
    private List<Long> productIds;  // 상품 ID 목록
}