package com.sparta.productservice.entity;

import com.sparta.common.entity.Timestamped;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Table(name = "product")
@NoArgsConstructor(access = PROTECTED) // JPA용 기본 생성자 (필수)
public class Product extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double price;

    private Integer stock; // 일반 상품 및 좌석별 재고 관리

    private String description;

    @Enumerated(EnumType.STRING)
    private ProductType type; // 상품 종류 (TICKET, GENERAL)

    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    private boolean isLimited; // 선착순 여부

    private LocalDateTime availableTime; // 구매 가능 시간

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ProductDetail productDetail;

    @Builder
    public Product(String name, double price, Integer stock, String description, ProductType type, ProductStatus status, boolean isLimited, LocalDateTime availableTime) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.description = description;
        this.type = type;
        this.status = status;
        this.isLimited = isLimited;
        this.availableTime = availableTime;
    }

    // 상품 정보 수정 비즈니스 메서드
    public void update(String name, double price, String description, String detailedDescription) {
        this.name = name;
        this.price = price;
        this.description = description;

        // 상세 설명 처리 로직
        if (this.productDetail != null) {
            // 1. 원래 상세 정보가 있었으면? -> 내용만 수정해라 (Update)
            this.productDetail.update(detailedDescription);
        } else if (detailedDescription != null && !detailedDescription.isEmpty()) {
            // 2. 원래 없었는데 이번에 들어왔으면? -> 새로 만들어서 붙여라 (Create & Link)
            ProductDetail newDetail = ProductDetail.builder()
                    .detailedDescription(detailedDescription)
                    .build();
            this.addDetail(newDetail);
        }
    }

    // Product와 Detail을 연결 연관관계 편의 메서드 (양방향 설정)
    public void addDetail(ProductDetail productDetail) {
        this.productDetail = productDetail;
        productDetail.setProduct(this);
    }

    // [비즈니스 로직] 재고 감소 (주문 시 호출)
    public void decreaseStock(int quantity) {
        if (this.stock < quantity) {
            throw new IllegalArgumentException("재고가 부족합니다. 현재 재고: " + this.stock);
        }
        this.stock -= quantity;
    }

    // [비즈니스 로직] 재고 증가 (주문 취소 시 호출)
    public void increaseStock(int quantity) {
        this.stock += quantity;
    }

    public enum ProductType {
        TICKET, GENERAL
    }

    public enum ProductStatus {
        OPEN, STOP
    }
}