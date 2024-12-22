package com.sparta.orderservice.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "wishlist")
@Data
public class Wishlist {

    @Id
    private Long userId;  // 유저 ID를 기본키로 설정

    @OneToMany(mappedBy = "wishlist", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // 직렬화 허용
    @ToString.Exclude // 무한 루프 방지
    private List<WishlistItem> items = new ArrayList<>(); // 빈 리스트로 초기화
}