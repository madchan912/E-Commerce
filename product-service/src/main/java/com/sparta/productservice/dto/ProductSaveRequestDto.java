package com.sparta.productservice.dto;

import com.sparta.productservice.entity.Product;
import com.sparta.productservice.entity.ProductDetail;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

@Getter
@NoArgsConstructor // JSON 파싱을 위해 기본 생성자는 필수
public class ProductSaveRequestDto {

    private String name;
    private Integer price;
    private Integer stock;
    private String description;

    @NotBlank(message = "상세 설명은 필수입니다.")
    private String detailedDescription;

    // DTO -> Entity 변환 메서드
    public Product toEntity() {
        // 1. Product(상품) 엔티티 생성 (아직 STOP 상태)
        Product product = Product.builder()
                .name(this.name)
                .price(this.price)
                .stock(this.stock)
                .description(this.description)
                .status(Product.ProductStatus.STOP) // 등록 시 기본값 판매중지
                .build();

        // 2. 상세 설명이 들어왔다면? ProductDetail 생성 및 연결
        if (this.detailedDescription != null && !this.detailedDescription.isEmpty()) {

            // 상세 엔티티 생성
            ProductDetail detail = ProductDetail.builder()
                    .detailedDescription(this.detailedDescription)
                    .build();

            product.addDetail(detail);
        }

        // 3. 상세 정보까지 품은(연결된) product 반환
        return product;
    }
}