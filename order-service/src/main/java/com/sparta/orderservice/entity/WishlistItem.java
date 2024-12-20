package com.sparta.orderservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Entity
@Data
public class WishlistItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 위시리스트와 다대일 관계 설정
    @JoinColumn(name = "wishlist_id", nullable = false)
    @JsonBackReference // 직렬화 제외
    @ToString.Exclude // 무한 루프 방지
    private Wishlist wishlist;

    @Column(nullable = false)
    private Long productId; // 상품 ID

    @Column(nullable = false)
    private int quantity; // 상품 수량
}
