package com.sparta.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders") // 테이블 이름을 "orders"로 설정하여 예약어 충돌 방지
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId; // 주문한 사용자 ID
    private Long productId; // 주문한 상품 ID
    private int quantity; // 주문한 상품 수량
    private LocalDateTime orderDate; // 주문일

    // 주문 상태를 Enum으로 관리
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING; // 기본 상태는 PENDING

    // 주문 상태를 관리하는 Enum
    public enum OrderStatus {
        PENDING, // 주문 접수됨
        SHIPPED, // 배송 중
        DELIVERED, // 배송 완료
        CANCELED, // 취소 완료
        RETURNED // 반품 완료
    }
}