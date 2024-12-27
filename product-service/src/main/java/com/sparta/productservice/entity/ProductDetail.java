package com.sparta.productservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ProductDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "product_id", nullable = false)
    @JsonBackReference
    private Product product;

    private String detailedDescription;

    private String imageUrl;

    private Integer stock; // 일반 상품 및 좌석별 재고 관리

    @ManyToOne
    @JoinColumn(name = "performance_id") // 공연 참조
    private Performance performance; // 공연 정보
}
