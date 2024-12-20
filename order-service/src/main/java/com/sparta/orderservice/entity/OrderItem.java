package com.sparta.orderservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Entity
@Data
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference // 직렬화 제외
    @ToString.Exclude // 무한 루프 방지
    private Order order; // 소속된 주문

    private Long productId; // 상품 ID

    private int quantity; // 상품 수량
}
