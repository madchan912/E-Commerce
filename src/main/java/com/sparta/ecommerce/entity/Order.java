package com.sparta.ecommerce.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Table(name = "orders") // 테이블 이름을 "orders"로 설정하여 예약어 충돌 방지
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId; // User와의 관계 설정을 위해 이후 수정 가능
    private Long productId; // Product와의 관계 설정을 위해 이후 수정 가능
    private int quantity;
    private LocalDateTime orderDate;
}
