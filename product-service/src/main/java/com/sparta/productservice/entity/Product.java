package com.sparta.productservice.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private double price;
    private String description;

    @Enumerated(EnumType.STRING)
    private ProductType type; // 상품 종류 (TICKET, GENERAL)

    private Boolean isLimited; // 선착순 여부

    private LocalDateTime availableTime; // 구매 가능 시간

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference //직렬화 허용
    private ProductDetail productDetail;

    public enum ProductType {
        TICKET, GENERAL
    }
}
