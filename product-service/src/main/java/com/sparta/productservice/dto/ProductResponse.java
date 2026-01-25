package com.sparta.productservice.dto;

import com.sparta.productservice.entity.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor // JSON 파싱을 위해 기본 생성자 필요
public class ProductResponse {
    private Long id;
    private String name;
    private Double price;
    private Integer stock;
    private String description;
    private Product.ProductStatus status; // 판매 상태 (OPEN, STOP)
    private Product.ProductType type;     // 상품 타입
    private LocalDateTime availableTime;  // 구매 가능 시간

    // ✅ 핵심: 엔티티를 받아서 DTO로 변환하는 생성자
    public ProductResponse(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.stock = product.getStock();
        this.description = product.getDescription();
        this.status = product.getStatus();
        this.type = product.getType();
        this.availableTime = product.getAvailableTime();
    }
}