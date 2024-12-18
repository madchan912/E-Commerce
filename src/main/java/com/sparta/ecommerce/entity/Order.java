package com.sparta.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders") // 테이블 이름을 "orders"로 설정하여 예약어 충돌 방지, PostgreSQL에서 에러 발생 방지
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId; // 주문한 사용자 ID
    private LocalDateTime orderDate = LocalDateTime.now(); // 주문 시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.ORDER_PLACED; // 주문 상태 기본값

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // 직렬화 허용
    @ToString.Exclude // 무한 루프 방지
    private List<OrderItem> orderItems; // 주문에 포함된 상품들

    public enum OrderStatus {
        ORDER_PLACED, SHIPPING, DELIVERED, CANCELED, RETURNED
    }
}