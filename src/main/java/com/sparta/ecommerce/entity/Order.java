package com.sparta.ecommerce.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
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
