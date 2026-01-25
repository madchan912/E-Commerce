package com.sparta.productservice.entity;

import com.sparta.common.entity.Timestamped;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Table(name = "product_detail")
@NoArgsConstructor(access = PROTECTED)
public class ProductDetail extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private String detailedDescription;

    private String imageUrl;

    // (공연 정보는 나중에 공연 파트에서 연결할 거라 잠시 둠)
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "performance_id")
    // private Performance performance;

    @Builder
    public ProductDetail(String detailedDescription, String imageUrl) {
        this.detailedDescription = detailedDescription;
        this.imageUrl = imageUrl;
    }

    // 연관관계 편의 메서드 (Product에서 호출됨)
    public void setProduct(Product product) {
        this.product = product;
    }

    // 상세 내용 수정 메서드
    public void update(String detailedDescription) {
        this.detailedDescription = detailedDescription;
    }
}
